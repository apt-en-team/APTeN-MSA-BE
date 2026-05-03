package com.apten.auth.infrastructure.kafka;

import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.auth.domain.repository.UserRepository;
import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.HouseholdMatchResultEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// household.match 이벤트를 수신해서 user.status를 ACTIVE / REJECTED로 갱신한다
@Slf4j
@Component
@RequiredArgsConstructor
public class HouseholdMatchConsumer {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // 세대 매칭 승인/거절 이벤트 수신
    @KafkaListener(
            topics = KafkaTopics.HOUSEHOLD,
            groupId = "auth-service"
    )
    public void consume(String message) {
        try {
            EventEnvelope<HouseholdMatchResultEventPayload> envelope = objectMapper.readValue(
                    message,
                    new TypeReference<>() {}
            );

            EventType eventType = envelope.getEventType();
            HouseholdMatchResultEventPayload payload = envelope.getPayload();

            // userId로 회원 조회
            User user = userRepository.findById(payload.getUserId())
                    .orElseGet(() -> {
                        log.warn("HouseholdMatchConsumer — 회원 없음 userId={}", payload.getUserId());
                        return null;
                    });

            if (user == null) return;

            // 승인이면 ACTIVE, 거절이면 REJECTED로 상태 갱신
            if (eventType == EventType.HOUSEHOLD_MATCH_APPROVED) {
                user.updateStatus(UserStatus.ACTIVE);
            } else if (eventType == EventType.HOUSEHOLD_MATCH_REJECTED) {
                user.updateStatus(UserStatus.REJECTED);
            }

        } catch (Exception e) {
            log.error("HouseholdMatchConsumer 처리 실패 — message={}", message, e);
        }
    }
}