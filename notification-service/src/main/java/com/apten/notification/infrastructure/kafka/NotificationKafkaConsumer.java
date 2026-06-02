package com.apten.notification.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.NotificationAdminBroadcastEventPayload;
import com.apten.common.kafka.payload.NotificationEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.notification.application.model.request.NotificationAdminBroadcastPostReq;
import com.apten.notification.application.model.request.NotificationPostReq;
import com.apten.notification.application.service.NotificationReferenceCacheService;
import com.apten.notification.application.service.NotificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// notification-service Kafka consumer: user_cache 동기화 + 시설예약 알림 이벤트 처리
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationReferenceCacheService notificationReferenceCacheService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    // 사용자 이벤트를 받아 user_cache를 최신 상태로 맞춘다.
    @KafkaListener(topics = KafkaTopics.USER, groupId = "notification-service-user-cache")
    public void consumeUserEvent(String message) {
        try {
            EventEnvelope<UserEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<UserEventPayload>>() {}
                    );

            log.info(
                    "Consumed user event. eventType={}, eventId={}",
                    eventEnvelope.getEventType(),
                    eventEnvelope.getEventId()
            );

            notificationReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume user event. message={}", message, exception);
        }
    }

    // 알림 생성 요청 이벤트를 받아 알림을 생성한다 (전 서비스 공용 토픽).
    @KafkaListener(topics = KafkaTopics.NOTIFICATION_REQUEST, groupId = "notification-service-request")
    public void consumeNotificationRequestEvent(String message) {
        try {
            EventEnvelope<JsonNode> envelope = objectMapper.readValue(
                    message,
                    new TypeReference<EventEnvelope<JsonNode>>() {}
            );

            String eventType = envelope.getEventType() == null ? null : envelope.getEventType().name();
            log.info("Consumed notification request event. eventType={}, eventId={}",
                    eventType, envelope.getEventId());

            if (EventType.NOTIFICATION_REQUESTED.name().equals(eventType)) {
                NotificationEventPayload payload = objectMapper.treeToValue(
                        envelope.getPayload(), NotificationEventPayload.class);
                notificationService.createNotification(toPostReq(payload));

            } else if (EventType.ADMIN_NOTIFICATION_REQUESTED.name().equals(eventType)) {
                NotificationAdminBroadcastEventPayload payload = objectMapper.treeToValue(
                        envelope.getPayload(), NotificationAdminBroadcastEventPayload.class);
                notificationService.createAdminBroadcastNotification(toBroadcastReq(payload));

            } else {
                log.warn("Unknown notification request eventType={}. message={}", eventType, message);
            }
        } catch (Exception exception) {
            log.error("Failed to consume notification request event. message={}", message, exception);
        }
    }

    private NotificationPostReq toPostReq(NotificationEventPayload payload) {
        return NotificationPostReq.builder()
                .receiverUserId(payload.getReceiverUserId())
                .complexId(payload.getComplexId())
                .type(payload.getType())
                .targetType(payload.getTargetType())
                .targetId(payload.getTargetId())
                .title(payload.getTitle())
                .content(payload.getContent())
                .linkPath(payload.getLinkPath())
                .payloadJson(payload.getPayloadJson())
                .build();
    }

    private NotificationAdminBroadcastPostReq toBroadcastReq(NotificationAdminBroadcastEventPayload payload) {
        return NotificationAdminBroadcastPostReq.builder()
                .complexId(payload.getComplexId())
                .type(payload.getType())
                .targetType(payload.getTargetType())
                .targetId(payload.getTargetId())
                .title(payload.getTitle())
                .content(payload.getContent())
                .linkPath(payload.getLinkPath())
                .payloadJson(payload.getPayloadJson())
                .build();
    }
}
