package com.apten.parkingvehicle.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // 문자열 JSON 메시지를 공통 envelope DTO로 파싱한다.
    private final ObjectMapper objectMapper;

    // user 이벤트를 받아 user cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.USER, groupId = "parking-vehicle-service-user-cache")
    public void consumeUserEvent(String message) {
        try {
            EventEnvelope<UserEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<UserEventPayload>>() {}
                    );
            log.info("Consumed user event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
            parkingVehicleReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume user event. message={}", message, exception);
        }
    }

    // household 이벤트를 받아 세대 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD, groupId = "parking-vehicle-service-household-cache")
    public void consumeHouseholdEvent(String message) {
        try {
            EventEnvelope<HouseholdEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<HouseholdEventPayload>>() {}
                    );
            log.info("Consumed household event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
            parkingVehicleReferenceCacheService.upsertHouseholdCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume household event. message={}", message, exception);
        }
    }

    // household member 이벤트를 받아 세대주 사용자 캐시를 보강한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD_MEMBER, groupId = "parking-vehicle-service-household-member-cache")
    public void consumeHouseholdMemberEvent(String message) {
        try {
            EventEnvelope<HouseholdMemberEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<HouseholdMemberEventPayload>>() {}
                    );
            log.info("Consumed household member event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
            parkingVehicleReferenceCacheService.syncHouseholdHeadUser(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume household member event. message={}", message, exception);
        }
    }
}
