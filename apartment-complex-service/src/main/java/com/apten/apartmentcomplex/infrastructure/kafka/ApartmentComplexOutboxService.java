package com.apten.apartmentcomplex.infrastructure.kafka;

import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.outbox.Outbox;
import com.apten.common.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 단지 원본 변경 이벤트를 Kafka로 바로 보내지 않고 Outbox row로 저장하는 서비스이다.
@Service
@RequiredArgsConstructor
public class ApartmentComplexOutboxService {

    // Outbox 엔티티를 저장해 relay가 나중에 Kafka로 전송할 수 있게 한다.
    private final OutboxRepository outboxRepository;

    // 공통 이벤트 envelope와 payload를 JSON 문자열로 바꿀 때 사용한다.
    private final ObjectMapper objectMapper;

    // 단지 생성 직후 같은 트랜잭션에서 생성 이벤트를 Outbox에 적재한다.
    public void saveCreatedEvent(ApartmentComplex apartmentComplex) {
        saveEvent(EventType.APARTMENT_COMPLEX_CREATED, apartmentComplex);
    }

    // 단지 수정 직후 같은 트랜잭션에서 수정 이벤트를 Outbox에 적재한다.
    public void saveUpdatedEvent(ApartmentComplex apartmentComplex) {
        saveEvent(EventType.APARTMENT_COMPLEX_UPDATED, apartmentComplex);
    }

    // 단지 상태가 INACTIVE로 바뀌는 흐름에서 비활성화 이벤트를 Outbox에 적재한다.
    public void saveDeactivatedEvent(ApartmentComplex apartmentComplex) {
        saveEvent(EventType.APARTMENT_COMPLEX_DEACTIVATED, apartmentComplex);
    }

    // 단지 이벤트 payload와 envelope를 만들고 Outbox 저장까지 한 번에 처리한다.
    private void saveEvent(EventType eventType, ApartmentComplex apartmentComplex) {
        // 현재 Kafka 계약의 apartment complex payload 필드만 사용한다.
        ApartmentComplexEventPayload payload = ApartmentComplexEventPayload.builder()
                .apartmentComplexId(apartmentComplex.getId())
                .name(apartmentComplex.getName())
                .status(resolveStatus(eventType, apartmentComplex))
                .build();

        // 기존 Kafka consumer가 읽던 공통 envelope 구조를 유지한다.
        EventEnvelope<ApartmentComplexEventPayload> eventEnvelope = EventEnvelope.<ApartmentComplexEventPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .version(1)
                .occurredAt(Instant.now())
                .producer("apartment-complex-service")
                .payload(payload)
                .build();

        // relay가 JSON 문자열을 그대로 Kafka에 보낼 수 있도록 payload를 문자열로 저장한다.
        String payloadJson = writePayload(eventEnvelope);

        // 원본 단지 PK를 aggregateId로 사용해서 같은 단지 이벤트가 같은 Kafka key를 갖게 한다.
        Outbox outbox = Outbox.builder()
                .topic(KafkaTopics.APARTMENT_COMPLEX)
                .aggregateId(apartmentComplex.getId())
                .eventType(eventType.name())
                .payload(payloadJson)
                .build();

        // 원본 엔티티 저장과 같은 트랜잭션 안에서 Outbox row를 남긴다.
        outboxRepository.save(outbox);
    }

    // 비활성화 이벤트는 명시적으로 INACTIVE 상태를 payload에 담아 소비 서비스가 상태를 맞추게 한다.
    private String resolveStatus(EventType eventType, ApartmentComplex apartmentComplex) {
        if (eventType == EventType.APARTMENT_COMPLEX_DEACTIVATED) {
            return ApartmentComplexStatus.INACTIVE.name();
        }
        return apartmentComplex.getStatus().name();
    }

    // JSON 직렬화 실패는 Outbox 누락으로 이어지므로 예외를 던져 원본 저장도 함께 롤백되게 한다.
    private String writePayload(EventEnvelope<ApartmentComplexEventPayload> eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("단지 이벤트 payload 직렬화에 실패했습니다.", exception);
        }
    }
}
