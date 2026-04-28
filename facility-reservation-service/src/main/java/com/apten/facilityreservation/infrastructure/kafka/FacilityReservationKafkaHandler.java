package com.apten.facilityreservation.infrastructure.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // 문자열 메시지를 envelope 객체로 직접 역직렬화한다
    private final ObjectMapper objectMapper;

    // user 이벤트를 받아 user cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.USER, groupId = "facility-reservation-service-user-cache")
    public void consumeUserEvent(String message) {
        try {
            EventEnvelope<UserEventPayload> eventEnvelope = objectMapper.readValue(
                    message,
                    new TypeReference<EventEnvelope<UserEventPayload>>() {}
            );

            log.info("Consumed user event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
            facilityReservationReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume user event. message={}", message, exception);
        }
    }

    // apartment complex 이벤트를 받아 단지 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "facility-reservation-service-apartment-complex-cache")
    public void consumeApartmentComplexEvent(String message) {
        try {
            EventEnvelope<ApartmentComplexEventPayload> eventEnvelope = objectMapper.readValue(
                    message,
                    new TypeReference<EventEnvelope<ApartmentComplexEventPayload>>() {}
            );

            log.info(
                    "Consumed apartment complex event. eventType={}, eventId={}",
                    eventEnvelope.getEventType(),
                    eventEnvelope.getEventId()
            );
            facilityReservationReferenceCacheService.upsertApartmentComplexCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume apartment complex event. message={}", message, exception);
        }
    }

    // household 이벤트를 받아 세대 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD, groupId = "facility-reservation-service-household-cache")
    public void consumeHouseholdEvent(String message) {
        try {
            EventEnvelope<HouseholdEventPayload> eventEnvelope = objectMapper.readValue(
                    message,
                    new TypeReference<EventEnvelope<HouseholdEventPayload>>() {}
            );

            log.info("Consumed household event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
            facilityReservationReferenceCacheService.upsertHouseholdCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume household event. message={}", message, exception);
        }
    }

    // household member 이벤트를 받아 세대 구성원 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD_MEMBER, groupId = "facility-reservation-service-household-member-cache")
    public void consumeHouseholdMemberEvent(String message) {
        try {
            EventEnvelope<HouseholdMemberEventPayload> eventEnvelope = objectMapper.readValue(
                    message,
                    new TypeReference<EventEnvelope<HouseholdMemberEventPayload>>() {}
            );

            log.info(
                    "Consumed household member event. eventType={}, eventId={}",
                    eventEnvelope.getEventType(),
                    eventEnvelope.getEventId()
            );
            facilityReservationReferenceCacheService.upsertHouseholdMemberCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume household member event. message={}", message, exception);
        }
    }
}
