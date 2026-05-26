package com.apten.auth.infrastructure.kafka;

import com.apten.auth.domain.entity.ResidentProfile;
import com.apten.auth.domain.entity.User;
import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.HouseholdMatchRequestEventPayload;
import com.apten.common.outbox.Outbox;
import com.apten.common.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 회원가입 후 세대 매칭 요청 이벤트를 Outbox에 저장하는 서비스이다.
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
public class AuthHouseholdMatchOutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    // 가입자의 세대 매칭 요청 이벤트를 같은 트랜잭션 안에서 Outbox에 적재한다.
    public void saveMatchRequestedEvent(User user, ResidentProfile residentProfile) {
        HouseholdMatchRequestEventPayload payload = HouseholdMatchRequestEventPayload.builder()
                .userId(user.getId())
                .complexId(residentProfile.getComplexId())
                .name(user.getName())
                .phone(user.getPhone())
                .birthDate(residentProfile.getBirthDate())
                .building(residentProfile.getBuilding())
                .unit(residentProfile.getUnit())
                .build();

        EventEnvelope<HouseholdMatchRequestEventPayload> eventEnvelope =
                EventEnvelope.<HouseholdMatchRequestEventPayload>builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType(EventType.HOUSEHOLD_MATCH_REQUESTED)
                        .version(1)
                        .occurredAt(Instant.now())
                        .producer("auth-service")
                        .payload(payload)
                        .build();

        Outbox outbox = Outbox.builder()
                .topic(KafkaTopics.HOUSEHOLD)
                .aggregateId(user.getId())
                .eventType(EventType.HOUSEHOLD_MATCH_REQUESTED.name())
                .payload(writePayload(eventEnvelope))
                .build();

        outboxRepository.save(outbox);
    }

    private String writePayload(EventEnvelope<HouseholdMatchRequestEventPayload> eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("세대 매칭 요청 이벤트 payload 직렬화에 실패했습니다.", exception);
        }
    }
}
