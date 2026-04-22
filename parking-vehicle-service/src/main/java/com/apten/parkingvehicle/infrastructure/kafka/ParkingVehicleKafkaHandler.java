package com.apten.parkingvehicle.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.parkingvehicle.application.service.ParkingVehicleReferenceCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// parking-vehicle-service가 참조 데이터 이벤트를 받아 캐시 테이블로 반영하는 consumer
@Slf4j
@Component
@RequiredArgsConstructor
public class ParkingVehicleKafkaHandler {

    // 캐시 반영은 application service에 위임한다
    private final ParkingVehicleReferenceCacheService parkingVehicleReferenceCacheService;

    // user 이벤트를 받아 user cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.USER, groupId = "parking-vehicle-service-user-cache")
    public void consumeUserEvent(EventEnvelope<UserEventPayload> eventEnvelope) {
        log.info("Consumed user event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
        parkingVehicleReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
    }

    // apartment complex 이벤트를 받아 단지 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "parking-vehicle-service-apartment-complex-cache")
    public void consumeApartmentComplexEvent(EventEnvelope<ApartmentComplexEventPayload> eventEnvelope) {
        log.info(
                "Consumed apartment complex event. eventType={}, eventId={}",
                eventEnvelope.getEventType(),
                eventEnvelope.getEventId()
        );
        parkingVehicleReferenceCacheService.upsertApartmentComplexCache(eventEnvelope.getPayload());
    }

    // household 이벤트를 받아 세대 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD, groupId = "parking-vehicle-service-household-cache")
    public void consumeHouseholdEvent(EventEnvelope<HouseholdEventPayload> eventEnvelope) {
        log.info("Consumed household event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
        parkingVehicleReferenceCacheService.upsertHouseholdCache(eventEnvelope.getPayload());
    }

    // household member 이벤트를 받아 세대 구성원 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD_MEMBER, groupId = "parking-vehicle-service-household-member-cache")
    public void consumeHouseholdMemberEvent(EventEnvelope<HouseholdMemberEventPayload> eventEnvelope) {
        log.info(
                "Consumed household member event. eventType={}, eventId={}",
                eventEnvelope.getEventType(),
                eventEnvelope.getEventId()
        );
        parkingVehicleReferenceCacheService.upsertHouseholdMemberCache(eventEnvelope.getPayload());
    }
}
