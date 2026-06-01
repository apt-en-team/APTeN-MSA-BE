package com.apten.notification.application.service;

import com.apten.notification.application.model.response.NotificationWebSocketRes;
import com.apten.notification.domain.entity.Notification;
import com.apten.notification.domain.repository.NotificationRepository;
import com.apten.notification.infrastructure.websocket.NotificationWebSocketSessionRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRealtimeService {

    private final NotificationRepository notificationRepository;
    private final NotificationWebSocketSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    // WebSocket 신규 알림 발송
    public void sendNewNotification(Notification notification) {
        // 실시간 전송 실패가 DB 알림 저장 결과를 흔들지 않도록 최상위에서 흡수한다
        try {
            doSendNewNotification(notification);
        } catch (Exception exception) {
            log.warn("알림 WebSocket 처리 실패. notificationId={}", notification == null ? null : notification.getId(), exception);
        }
    }

    private void doSendNewNotification(Notification notification) {
        if (notification == null || notification.getUserId() == null) {
            return;
        }

        Long userId = notification.getUserId();
        // 사용자가 접속 중이 아니면 목록 API로 확인할 수 있으므로 조용히 종료한다
        if (!sessionRegistry.hasSession(userId)) {
            return;
        }

        String payload = toPayload(notification);
        if (payload == null) {
            return;
        }

        for (WebSocketSession session : sessionRegistry.findByUserId(userId)) {
            send(session, payload);
        }
    }

    private String toPayload(Notification notification) {
        // 신규 알림과 함께 최신 미읽음 수를 보내 배지를 즉시 동기화한다
        // afterCommit 이후 매번 count를 조회하므로 대량 발송 단계에서는 커넥션 풀 압박을 별도로 점검한다
        long unreadCount = notificationRepository.countByUserIdAndIsRead(notification.getUserId(), false);
        NotificationWebSocketRes response = NotificationWebSocketRes.builder()
                .type(notification.getType().name())
                .typeCode(notification.getType().getCode())
                .typeValue(notification.getType().getValue())
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .linkPath(notification.getLinkPath())
                .unreadCount(unreadCount)
                .createdAt(notification.getCreatedAt() == null ? LocalDateTime.now() : notification.getCreatedAt())
                .build();

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException exception) {
            log.warn("알림 WebSocket payload 직렬화 실패. notificationId={}", notification.getId(), exception);
            return null;
        }
    }

    private void send(WebSocketSession session, String payload) {
        try {
            if (!session.isOpen()) {
                // 닫힌 세션은 이후 전송 대상에서 제외한다
                sessionRegistry.remove(session);
                return;
            }
            session.sendMessage(new TextMessage(payload));
        } catch (Exception exception) {
            sessionRegistry.remove(session);
            log.warn("알림 WebSocket 전송 실패. sessionId={}", session.getId(), exception);
        }
    }
}
