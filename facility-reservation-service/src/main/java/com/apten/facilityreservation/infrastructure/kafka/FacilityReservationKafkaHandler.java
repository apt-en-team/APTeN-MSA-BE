package com.apten.facilityreservation.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.facilityreservation.application.service.FacilityReservationReferenceCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// facility-reservation-service가 참조 데이터 이벤트를 받아 캐시 테이블로 반영하는 consumer
@Slf4j
@Component
@RequiredArgsConstructor
public class FacilityReservationKafkaHandler {

    // 캐시 반영은 application service에 위임한다
    private final FacilityReservationReferenceCacheService facilityReservationReferenceCacheService;

    // user 이벤트를 받아 user cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.USER, groupId = "facility-reservation-service-user-cache")
    public void consumeUserEvent(EventEnvelope<UserEventPayload> eventEnvelope) {
        log.info("Consumed user event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
        facilityReservationReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
    }

    // apartment complex 이벤트를 받아 단지 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "facility-reservation-service-apartment-complex-cache")
    public void consumeApartmentComplexEvent(EventEnvelope<ApartmentComplexEventPayload> eventEnvelope) {
        log.info(
                "Consumed apartment complex event. eventType={}, eventId={}",
                eventEnvelope.getEventType(),
                eventEnvelope.getEventId()
        );
        facilityReservationReferenceCacheService.upsertApartmentComplexCache(eventEnvelope.getPayload());
    }

    // household 이벤트를 받아 세대 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD, groupId = "facility-reservation-service-household-cache")
    public void consumeHouseholdEvent(EventEnvelope<HouseholdEventPayload> eventEnvelope) {
        log.info("Consumed household event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
        facilityReservationReferenceCacheService.upsertHouseholdCache(eventEnvelope.getPayload());
    }

    // household member 이벤트를 받아 세대 구성원 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD_MEMBER, groupId = "facility-reservation-service-household-member-cache")
    public void consumeHouseholdMemberEvent(EventEnvelope<HouseholdMemberEventPayload> eventEnvelope) {
        log.info(
                "Consumed household member event. eventType={}, eventId={}",
                eventEnvelope.getEventType(),
                eventEnvelope.getEventId()
        );
        facilityReservationReferenceCacheService.upsertHouseholdMemberCache(eventEnvelope.getPayload());
    }
}
