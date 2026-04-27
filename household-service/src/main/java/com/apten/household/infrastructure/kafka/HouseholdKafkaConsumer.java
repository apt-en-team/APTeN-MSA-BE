package com.apten.household.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.household.application.service.HouseholdReferenceCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// 외부 서비스 이벤트를 수신해 household-service 캐시를 갱신하는 consumer이다.
@Slf4j
@Component
@RequiredArgsConstructor
public class HouseholdKafkaConsumer {

    // 캐시 반영 전용 서비스이다.
    private final HouseholdReferenceCacheService householdReferenceCacheService;

    // 사용자 이벤트를 받아 user_cache를 갱신한다.
    @KafkaListener(topics = KafkaTopics.USER, groupId = "household-service-user-cache")
    public void consumeUserEvent(EventEnvelope<UserEventPayload> eventEnvelope) {
        // 수신한 이벤트 기본 정보를 로그로 남긴다.
        log.info(
                "Consumed user event. eventType={}, eventId={}",
                eventEnvelope.getEventType(),
                eventEnvelope.getEventId()
        );

        // 외부 사용자 원본 값을 user_cache에 반영한다.
        householdReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
    }

    // 단지 이벤트를 받아 complex_cache를 갱신한다.
    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "household-service-complex-cache")
    public void consumeApartmentComplexEvent(EventEnvelope<ApartmentComplexEventPayload> eventEnvelope) {
        // 수신한 이벤트 기본 정보를 로그로 남긴다.
        log.info(
                "Consumed apartment complex event. eventType={}, eventId={}",
                eventEnvelope.getEventType(),
                eventEnvelope.getEventId()
        );

        // 외부 단지 원본 값을 complex_cache에 반영한다.
        householdReferenceCacheService.upsertComplexCache(eventEnvelope.getPayload());
    }
}
