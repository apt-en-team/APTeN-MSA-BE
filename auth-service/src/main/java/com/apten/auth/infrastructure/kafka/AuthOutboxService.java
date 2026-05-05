package com.apten.auth.infrastructure.kafka;

import com.apten.auth.domain.entity.User;
import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.common.outbox.Outbox;
import com.apten.common.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 사용자 원본 변경 이벤트를 Kafka로 바로 보내지 않고 Outbox row로 저장하는 서비스이다.
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
public class AuthOutboxService {

    // Outbox 엔티티를 저장해 relay가 나중에 Kafka로 전송할 수 있게 한다.
    private final OutboxRepository outboxRepository;

    // 공통 이벤트 envelope와 payload를 JSON 문자열로 바꿀 때 사용한다.
    private final ObjectMapper objectMapper;

    // 회원 생성 직후 같은 트랜잭션에서 USER_CREATED 이벤트를 Outbox에 적재한다.
    public void saveCreatedEvent(User user, Long complexId) {
        saveEvent(EventType.USER_CREATED, user, complexId);
    }

    // 회원 정보 수정 직후 같은 트랜잭션에서 USER_UPDATED 이벤트를 Outbox에 적재한다.
    public void saveUpdatedEvent(User user, Long complexId) {
        saveEvent(EventType.USER_UPDATED, user, complexId);
    }

    // 회원 삭제 또는 탈퇴 시 USER_DELETED 이벤트를 Outbox에 적재한다.
    public void saveDeletedEvent(User user, Long complexId) {
        saveEvent(EventType.USER_DELETED, user, complexId);
    }

    // 사용자 이벤트 payload와 envelope를 만들고 Outbox 저장까지 한 번에 처리한다.
    private void saveEvent(EventType eventType, User user, Long complexId) {
        // 현재 Kafka 계약의 user cache payload 필드만 사용한다.
        UserEventPayload payload = UserEventPayload.builder()
                .userId(user.getId())
                .complexId(complexId)
                .name(user.getName())
                .phone(user.getPhone())
//                .birthDate(user.getBirthDate())
                .role(user.getRole().name())
                .status(user.getStatus().name())
//                .apartmentComplexId(user.getComplexId())
                .isDeleted(user.getIsDeleted())
                .build();

        // 기존 Kafka consumer가 읽던 공통 envelope 구조를 유지한다.
        EventEnvelope<UserEventPayload> eventEnvelope = EventEnvelope.<UserEventPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .version(1)
                .occurredAt(Instant.now())
                .producer("auth-service")
                .payload(payload)
                .build();

        // relay가 JSON 문자열을 그대로 Kafka에 보낼 수 있도록 payload를 문자열로 저장한다.
        String payloadJson = writePayload(eventEnvelope);

        // 원본 사용자 PK를 aggregateId로 사용해서 같은 사용자 이벤트가 같은 Kafka key를 갖게 한다.
        Outbox outbox = Outbox.builder()
                .topic(KafkaTopics.USER)
                .aggregateId(user.getId())
                .eventType(eventType.name())
                .payload(payloadJson)
                .build();

        // 원본 엔티티 저장과 같은 트랜잭션 안에서 Outbox row를 남긴다.
        outboxRepository.save(outbox);
    }

    // JSON 직렬화 실패는 Outbox 누락으로 이어지므로 예외를 던져 원본 저장도 함께 롤백되게 한다.
    private String writePayload(EventEnvelope<UserEventPayload> eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("사용자 이벤트 payload 직렬화에 실패했습니다.", exception);
        }
    }
}
