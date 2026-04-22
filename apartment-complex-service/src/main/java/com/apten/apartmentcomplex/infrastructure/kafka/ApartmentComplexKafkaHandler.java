package com.apten.apartmentcomplex.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// apartment-complex-service가 단지 원본 변경을 Kafka로 발행하는 producer
// 단지 원본은 이 서비스만 수정하고 다른 서비스는 캐시만 갱신한다
@Slf4j
@Component
@RequiredArgsConstructor
public class ApartmentComplexKafkaHandler {

    // apartment complex 이벤트 발행 전용 KafkaTemplate
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 단지 생성 이벤트를 발행한다
    public void publishApartmentComplexCreated(ApartmentComplexEventPayload payload) {
        publish(EventType.APARTMENT_COMPLEX_CREATED, payload);
    }

    // 단지 수정 이벤트를 발행한다
    public void publishApartmentComplexUpdated(ApartmentComplexEventPayload payload) {
        publish(EventType.APARTMENT_COMPLEX_UPDATED, payload);
    }

    // 단지 비활성화 이벤트를 발행한다
    public void publishApartmentComplexDeactivated(ApartmentComplexEventPayload payload) {
        publish(EventType.APARTMENT_COMPLEX_DEACTIVATED, payload);
    }

    // apartment complex topic에 공통 envelope를 감싸 발행한다
    private void publish(EventType eventType, ApartmentComplexEventPayload payload) {
        EventEnvelope<ApartmentComplexEventPayload> eventEnvelope = EventEnvelope.<ApartmentComplexEventPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .version(1)
                .occurredAt(Instant.now())
                .producer("apartment-complex-service")
                .payload(payload)
                .build();

        kafkaTemplate.send(
                KafkaTopics.APARTMENT_COMPLEX,
                String.valueOf(payload.getApartmentComplexId()),
                eventEnvelope
        );
        log.info(
                "Published apartment complex event. eventType={}, apartmentComplexId={}",
                eventType,
                payload.getApartmentComplexId()
        );
    }
}
