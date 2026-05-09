package com.apten.auth.infrastructure.kafka;

import com.apten.auth.application.service.AuthReferenceCacheService;
import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// auth-service가 단지 이벤트를 받아 complex_cache를 반영하는 consumer이다.
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthKafkaConsumer {

    // 단지 캐시 반영 서비스이다.
    private final AuthReferenceCacheService authReferenceCacheService;

    // 문자열 JSON 메시지를 공통 envelope DTO로 파싱한다.
    private final ObjectMapper objectMapper;

    // 단지 이벤트를 받아 auth-service의 complex_cache를 최신 상태로 맞춘다.
    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "auth-service-complex-cache")
    public void consumeApartmentComplexEvent(String message) {
        try {
            EventEnvelope<ApartmentComplexEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<ApartmentComplexEventPayload>>() {}
                    );

            log.info(
                    "Consumed apartment complex event. eventType={}, eventId={}",
                    eventEnvelope.getEventType(),
                    eventEnvelope.getEventId()
            );

            // 수신한 단지 원본 값을 complex_cache에 반영한다.
            authReferenceCacheService.upsertComplexCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume apartment complex event. message={}", message, exception);
        }
    }
}
