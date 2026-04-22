package com.apten.auth.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.UserEventPayload;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// auth-service가 user 원본 변경을 Kafka로 발행하는 producer
// 실제 회원 로직은 아직 없고 이벤트 계약에 맞는 발행 구조만 먼저 고정한다
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthKafkaHandler {

    // user 이벤트 발행 전용 KafkaTemplate
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 사용자 생성 이벤트를 발행한다
    public void publishUserCreated(UserEventPayload payload) {
        publish(EventType.USER_CREATED, payload);
    }

    // 사용자 수정 이벤트를 발행한다
    public void publishUserUpdated(UserEventPayload payload) {
        publish(EventType.USER_UPDATED, payload);
    }

    // 사용자 비활성화 이벤트를 발행한다
    public void publishUserDeactivated(UserEventPayload payload) {
        publish(EventType.USER_DEACTIVATED, payload);
    }

    // user topic에 공통 envelope를 감싸 발행한다
    private void publish(EventType eventType, UserEventPayload payload) {
        EventEnvelope<UserEventPayload> eventEnvelope = EventEnvelope.<UserEventPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .version(1)
                .occurredAt(Instant.now())
                .producer("auth-service")
                .payload(payload)
                .build();

        kafkaTemplate.send(KafkaTopics.USER, String.valueOf(payload.getUserId()), eventEnvelope);
        log.info("Published user event. eventType={}, userId={}", eventType, payload.getUserId());
    }
}
