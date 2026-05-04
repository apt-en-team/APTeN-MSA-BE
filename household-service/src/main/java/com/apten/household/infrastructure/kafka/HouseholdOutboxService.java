package com.apten.household.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMatchResultEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.outbox.Outbox;
import com.apten.common.outbox.OutboxRepository;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.entity.HouseholdMember;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 세대 서비스의 원본 변경 이벤트를 Outbox에 적재하는 서비스이다.
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
public class HouseholdOutboxService {

    // Outbox row 저장소이다.
    private final OutboxRepository outboxRepository;

    // EventEnvelope를 JSON 문자열로 직렬화한다.
    private final ObjectMapper objectMapper;

    // 세대 생성 이벤트를 Outbox에 적재한다.
    public void saveHouseholdCreatedEvent(Household household) {
        // 세대 생성 payload를 만들고 공통 저장 메서드에 위임한다.
        saveHouseholdEvent(EventType.HOUSEHOLD_CREATED, household);
    }

    // 세대 수정 이벤트를 Outbox에 적재한다.
    public void saveHouseholdUpdatedEvent(Household household) {
        // 세대 수정 payload를 만들고 공통 저장 메서드에 위임한다.
        saveHouseholdEvent(EventType.HOUSEHOLD_UPDATED, household);
    }

    // 세대 비활성화 이벤트를 Outbox에 적재한다.
    public void saveHouseholdDeactivatedEvent(Household household) {
        // 세대 비활성화 payload를 만들고 공통 저장 메서드에 위임한다.
        saveHouseholdEvent(EventType.HOUSEHOLD_DEACTIVATED, household);
    }

    // 세대원 생성 이벤트를 Outbox에 적재한다.
    public void saveHouseholdMemberCreatedEvent(HouseholdMember householdMember) {
        // 세대원 생성 payload를 만들고 공통 저장 메서드에 위임한다.
        saveHouseholdMemberEvent(EventType.HOUSEHOLD_MEMBER_CREATED, householdMember);
    }

    // 세대원 수정 이벤트를 Outbox에 적재한다.
    public void saveHouseholdMemberUpdatedEvent(HouseholdMember householdMember) {
        // 세대원 수정 payload를 만들고 공통 저장 메서드에 위임한다.
        saveHouseholdMemberEvent(EventType.HOUSEHOLD_MEMBER_UPDATED, householdMember);
    }

    // 세대원 삭제 이벤트를 Outbox에 적재한다.
    public void saveHouseholdMemberRemovedEvent(HouseholdMember householdMember) {
        // 세대원 삭제 payload를 만들고 공통 저장 메서드에 위임한다.
        saveHouseholdMemberEvent(EventType.HOUSEHOLD_MEMBER_REMOVED, householdMember);
    }

    // 세대주 변경 이벤트는 세대원 수정 이벤트로 같이 적재한다.
    public void saveHouseholdHeadChangedEvent(HouseholdMember householdMember) {
        // 세대주 변경도 세대원 역할 변경이므로 수정 이벤트로 적재한다.
        saveHouseholdMemberUpdatedEvent(householdMember);
    }

    // 세대 매칭 승인 결과 이벤트 적재 준비 메서드이다.
    public void saveMatchApprovedEvent(HouseholdMatchResultEventPayload payload) {
        // TODO 세대 매칭 승인 결과용 공통 EventType과 topic을 정의한 뒤 Outbox 적재를 연결한다.
    }

    // 세대 매칭 거절 결과 이벤트 적재 준비 메서드이다.
    public void saveMatchRejectedEvent(HouseholdMatchResultEventPayload payload) {
        // TODO 세대 매칭 거절 결과용 공통 EventType과 topic을 정의한 뒤 Outbox 적재를 연결한다.
    }

    // 세대 이벤트 payload를 만들어 Outbox에 저장한다.
    private void saveHouseholdEvent(EventType eventType, Household household) {
        // 세대 원본 필드를 공통 payload에 맞춰 채운다.
        HouseholdEventPayload payload = HouseholdEventPayload.builder()
                .householdId(household.getId())
                .apartmentComplexId(household.getComplexId())
                .buildingNo(household.getBuilding())
                .unitNo(household.getUnit())
                .status(household.getStatus().name())
                .build();

        // 공통 Outbox 저장 메서드로 위임한다.
        saveOutboxEvent(KafkaTopics.HOUSEHOLD, eventType, household.getId(), payload);
    }

    // 세대원 이벤트 payload를 만들어 Outbox에 저장한다.
    private void saveHouseholdMemberEvent(EventType eventType, HouseholdMember householdMember) {
        // 세대원 상태는 활성 여부를 기반으로 단순 문자열로 적재한다.
        HouseholdMemberEventPayload payload = HouseholdMemberEventPayload.builder()
                .householdMemberId(householdMember.getId())
                .householdId(householdMember.getHouseholdId())
                .userId(householdMember.getUserId())
                .memberRole(householdMember.getRole().name())
                .status(Boolean.TRUE.equals(householdMember.getIsActive()) ? "ACTIVE" : "INACTIVE")
                .isPrimary(householdMember.getRole().name().equals("HEAD"))
                .build();

        // 공통 Outbox 저장 메서드로 위임한다.
        saveOutboxEvent(KafkaTopics.HOUSEHOLD_MEMBER, eventType, householdMember.getId(), payload);
    }

    // payload를 EventEnvelope로 감싼 뒤 Outbox row로 저장한다.
    private <T> void saveOutboxEvent(String topic, EventType eventType, Long aggregateId, T payload) {
        // 공통 이벤트 envelope를 만든다.
        EventEnvelope<T> eventEnvelope = EventEnvelope.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .version(1)
                .occurredAt(Instant.now())
                .producer("household-service")
                .payload(payload)
                .build();

        try {
            // EventEnvelope를 JSON 문자열로 직렬화한다.
            String jsonPayload = objectMapper.writeValueAsString(eventEnvelope);

            // Relay가 읽을 Outbox row를 저장한다.
            outboxRepository.save(
                    Outbox.builder()
                            .topic(topic)
                            .aggregateId(aggregateId)
                            .eventType(eventType.name())
                            .payload(jsonPayload)
                            .build()
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize household outbox payload", e);
        }
    }
}
