package com.apten.apartmentcomplex.infrastructure.kafka;

import com.apten.apartmentcomplex.application.service.ApartmentComplexReferenceCacheService;
import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.UserEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// apartment-complex-service가 USER topic을 구독해 user_cache를 반영하는 consumer이다.
// 단지 이벤트 발행은 Outbox가 담당하고 이 클래스는 받는 쪽 역할만 맡는다.
@Slf4j
@Component
@RequiredArgsConstructor
public class ApartmentComplexKafkaConsumer {

    // 수신한 user 이벤트를 user_cache upsert 로직에 위임한다.
    private final ApartmentComplexReferenceCacheService referenceCacheService;

    // 문자열 JSON 메시지를 공통 envelope DTO로 파싱한다.
    private final ObjectMapper objectMapper;

    // USER topic 이벤트를 받아 관리자 검증용 user_cache를 최신 상태로 맞춘다.
    @KafkaListener(
            topics = KafkaTopics.USER,
            groupId = "apartment-complex-service-user-cache"
    )
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

            // eventType 기준으로 생성, 수정, 삭제성 이벤트를 각각 맞는 방식으로 반영한다.
            referenceCacheService.handleUserEvent(eventEnvelope.getEventType(), eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume user event. message={}", message, exception);
        }
    }
}
