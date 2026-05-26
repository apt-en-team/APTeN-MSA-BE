package com.apten.notification.infrastructure.websocket;

import com.apten.common.constants.HeaderConstants;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class NotificationWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    public static final String USER_ID_ATTRIBUTE = "notificationUserId";

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        // 기본 인증 기준은 Gateway가 JWT 검증 후 주입한 X-User-Id 헤더이다
        Long userId = parseUserId(request.getHeaders().getFirst(HeaderConstants.X_USER_ID));
        if (userId == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String role = request.getHeaders().getFirst(HeaderConstants.X_USER_ROLE);
        // MASTER는 user_cache에 저장될 수 있지만 단지별 실시간 알림 수신 대상은 아니다
        if ("MASTER".equalsIgnoreCase(role)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        // 세션 생성 이후 handler가 userId를 꺼내 등록할 수 있도록 handshake attribute에 저장한다
        attributes.put(USER_ID_ATTRIBUTE, userId);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }

    private Long parseUserId(String userId) {
        // 헤더가 없거나 숫자 Long으로 변환할 수 없으면 인증 실패로 본다
        if (userId == null || userId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
