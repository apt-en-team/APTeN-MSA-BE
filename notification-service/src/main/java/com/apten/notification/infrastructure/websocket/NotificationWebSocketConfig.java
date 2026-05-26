package com.apten.notification.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class NotificationWebSocketConfig implements WebSocketConfigurer {

    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final NotificationWebSocketHandshakeInterceptor notificationWebSocketHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Gateway의 ws:// 라우트가 최종적으로 notification-service의 이 endpoint로 연결된다
        registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
                .addInterceptors(notificationWebSocketHandshakeInterceptor)
                // 실제 운영 origin 제한은 FE 배포 도메인이 확정된 뒤 좁힌다
                .setAllowedOriginPatterns("*");
    }
}
