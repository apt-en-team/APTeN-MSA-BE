package com.apten.household.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdMatchRequestEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.household.application.service.HouseholdMatchService;
import com.apten.household.application.service.HouseholdReferenceCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// household-service가 외부 이벤트를 수신해 캐시와 세대 매칭을 처리하는 consumer이다.
@Slf4j
@Component
@RequiredArgsConstructor
public class HouseholdKafkaConsumer {

    // 캐시 반영 전용 서비스이다.
    private final HouseholdReferenceCacheService householdReferenceCacheService;

    // 회원가입 기반 세대 매칭 요청을 처리하는 서비스이다.
    private final HouseholdMatchService householdMatchService;

    // 문자열 JSON 메시지를 공통 envelope DTO로 파싱한다.
    private final ObjectMapper objectMapper;

    // 사용자 이벤트를 받아 user_cache를 갱신한다.
    @KafkaListener(topics = KafkaTopics.USER, groupId = "household-service-user-cache")
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

            householdReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume user event. message={}", message, exception);
        }
    }

    // 단지 이벤트를 받아 complex_cache를 갱신한다.
    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "household-service-complex-cache")
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

            householdReferenceCacheService.upsertComplexCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume apartment complex event. message={}", message, exception);
        }
    }

    // 회원가입 후 세대 매칭 요청 이벤트를 받아 자동승인 또는 승인대기 요청을 생성한다.
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD, groupId = "household-service-match-request")
    public void consumeHouseholdMatchRequestedEvent(String message) {
        try {
            EventEnvelope<HouseholdMatchRequestEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<HouseholdMatchRequestEventPayload>>() {}
                    );

            if (eventEnvelope.getEventType() != EventType.HOUSEHOLD_MATCH_REQUESTED) {
                return;
            }

            log.info(
                    "Consumed household match request event. eventType={}, eventId={}",
                    eventEnvelope.getEventType(),
                    eventEnvelope.getEventId()
            );

            householdMatchService.createMatchRequest(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume household match request event. message={}", message, exception);
        }
    }

    // TODO 차량 요약 이벤트 계약이 확정되면 VEHICLE topic consumer를 추가한다.
    // TODO 시설 이용 요약 이벤트 계약이 확정되면 FACILITY_USAGE topic consumer를 추가한다.
    // TODO 방문차량 집계 이벤트 계약이 확정되면 VISITOR_USAGE topic consumer를 추가한다.
}
