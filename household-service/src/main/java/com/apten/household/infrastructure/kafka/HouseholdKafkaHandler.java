package com.apten.household.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// household-service가 세대와 세대 구성원 원본 변경을 Kafka로 발행하는 producer
// 세대 관련 원본은 이 서비스만 바꾸고 다른 서비스는 캐시만 업데이트한다
@Slf4j
@Component
@RequiredArgsConstructor
public class HouseholdKafkaHandler {

    // household 계열 이벤트 발행 전용 KafkaTemplate
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 세대 생성 이벤트를 발행한다
    public void publishHouseholdCreated(HouseholdEventPayload payload) {
        publishHousehold(EventType.HOUSEHOLD_CREATED, payload);
    }

    // 세대 수정 이벤트를 발행한다
    public void publishHouseholdUpdated(HouseholdEventPayload payload) {
        publishHousehold(EventType.HOUSEHOLD_UPDATED, payload);
    }

    // 세대 비활성화 이벤트를 발행한다
    public void publishHouseholdDeactivated(HouseholdEventPayload payload) {
        publishHousehold(EventType.HOUSEHOLD_DEACTIVATED, payload);
    }

    // 세대 구성원 생성 이벤트를 발행한다
    public void publishHouseholdMemberCreated(HouseholdMemberEventPayload payload) {
        publishHouseholdMember(EventType.HOUSEHOLD_MEMBER_CREATED, payload);
    }

    // 세대 구성원 수정 이벤트를 발행한다
    public void publishHouseholdMemberUpdated(HouseholdMemberEventPayload payload) {
        publishHouseholdMember(EventType.HOUSEHOLD_MEMBER_UPDATED, payload);
    }

    // 세대 구성원 제거 이벤트를 발행한다
    public void publishHouseholdMemberRemoved(HouseholdMemberEventPayload payload) {
        publishHouseholdMember(EventType.HOUSEHOLD_MEMBER_REMOVED, payload);
    }

    // household topic에 공통 envelope를 감싸 발행한다
    private void publishHousehold(EventType eventType, HouseholdEventPayload payload) {
        EventEnvelope<HouseholdEventPayload> eventEnvelope = EventEnvelope.<HouseholdEventPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .version(1)
                .occurredAt(Instant.now())
                .producer("household-service")
                .payload(payload)
                .build();

        kafkaTemplate.send(KafkaTopics.HOUSEHOLD, String.valueOf(payload.getHouseholdId()), eventEnvelope);
        log.info("Published household event. eventType={}, householdId={}", eventType, payload.getHouseholdId());
    }

    // household member topic에 공통 envelope를 감싸 발행한다
    private void publishHouseholdMember(EventType eventType, HouseholdMemberEventPayload payload) {
        EventEnvelope<HouseholdMemberEventPayload> eventEnvelope = EventEnvelope.<HouseholdMemberEventPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .version(1)
                .occurredAt(Instant.now())
                .producer("household-service")
                .payload(payload)
                .build();

        kafkaTemplate.send(
                KafkaTopics.HOUSEHOLD_MEMBER,
                String.valueOf(payload.getHouseholdMemberId()),
                eventEnvelope
        );
        log.info(
                "Published household member event. eventType={}, householdMemberId={}",
                eventType,
                payload.getHouseholdMemberId()
        );
    }
}
