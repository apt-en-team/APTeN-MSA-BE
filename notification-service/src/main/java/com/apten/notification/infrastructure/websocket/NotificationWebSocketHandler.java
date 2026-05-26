package com.apten.notification.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final NotificationWebSocketSessionRegistry sessionRegistry;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // HandshakeInterceptor가 검증한 userId를 세션 저장소에 등록한다
        Long userId = (Long) session.getAttributes().get(NotificationWebSocketHandshakeInterceptor.USER_ID_ATTRIBUTE);
        if (userId != null) {
            sessionRegistry.register(userId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 브라우저 탭 종료나 네트워크 종료 시 더 이상 전송 대상에 포함하지 않는다
        sessionRegistry.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        // 전송 오류가 난 세션은 다음 알림 전송에서 제외한다
        sessionRegistry.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 1차 WebSocket은 서버 발송 전용이며 클라이언트 메시지는 처리하지 않는다.
    }
}
