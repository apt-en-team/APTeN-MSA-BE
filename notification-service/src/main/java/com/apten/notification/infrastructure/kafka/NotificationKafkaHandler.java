package com.apten.notification.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.notification.application.service.NotificationReferenceCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// notification-service가 참조 데이터 이벤트를 받아 캐시 테이블로 반영하는 consumer
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaHandler {

    // 캐시 반영은 application service에 위임한다
    private final NotificationReferenceCacheService notificationReferenceCacheService;

    // user 이벤트를 받아 user cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.USER, groupId = "notification-service-user-cache")
    public void consumeUserEvent(EventEnvelope<UserEventPayload> eventEnvelope) {
        log.info("Consumed user event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
        notificationReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
    }

    // apartment complex 이벤트를 받아 단지 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "notification-service-apartment-complex-cache")
    public void consumeApartmentComplexEvent(EventEnvelope<ApartmentComplexEventPayload> eventEnvelope) {
        log.info(
                "Consumed apartment complex event. eventType={}, eventId={}",
                eventEnvelope.getEventType(),
                eventEnvelope.getEventId()
        );
        notificationReferenceCacheService.upsertApartmentComplexCache(eventEnvelope.getPayload());
    }

    // household 이벤트를 받아 세대 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD, groupId = "notification-service-household-cache")
    public void consumeHouseholdEvent(EventEnvelope<HouseholdEventPayload> eventEnvelope) {
        log.info("Consumed household event. eventType={}, eventId={}", eventEnvelope.getEventType(), eventEnvelope.getEventId());
        notificationReferenceCacheService.upsertHouseholdCache(eventEnvelope.getPayload());
    }

    // household member 이벤트를 받아 세대 구성원 cache를 갱신한다
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD_MEMBER, groupId = "notification-service-household-member-cache")
    public void consumeHouseholdMemberEvent(EventEnvelope<HouseholdMemberEventPayload> eventEnvelope) {
        log.info(
                "Consumed household member event. eventType={}, eventId={}",
                eventEnvelope.getEventType(),
                eventEnvelope.getEventId()
        );
        notificationReferenceCacheService.upsertHouseholdMemberCache(eventEnvelope.getPayload());
    }
}
