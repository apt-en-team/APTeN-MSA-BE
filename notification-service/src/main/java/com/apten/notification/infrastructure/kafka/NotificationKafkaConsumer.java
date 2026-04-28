package com.apten.notification.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.notification.application.service.NotificationReferenceCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// notification-service가 사용자 이벤트를 받아 user_cache를 반영하는 consumer이다.
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    // 사용자 캐시 반영 서비스이다.
    private final NotificationReferenceCacheService notificationReferenceCacheService;

    // 문자열 JSON 메시지를 공통 envelope DTO로 파싱한다.
    private final ObjectMapper objectMapper;

    // 사용자 이벤트를 받아 notification-service의 user_cache를 최신 상태로 맞춘다.
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

            // 수신한 사용자 원본 값을 user_cache에 반영한다.
            notificationReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume user event. message={}", message, exception);
        }
    }
}
