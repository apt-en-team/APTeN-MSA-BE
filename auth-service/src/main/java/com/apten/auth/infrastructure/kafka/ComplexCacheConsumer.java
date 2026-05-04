package com.apten.auth.infrastructure.kafka;

import com.apten.auth.domain.entity.ComplexCache;
import com.apten.auth.domain.enums.ComplexCacheStatus;
import com.apten.auth.domain.repository.ComplexCacheRepository;
import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// apartment-complex.v1 토픽을 소비해서 complex_cache 테이블을 동기화한다
@Slf4j
@Component
@RequiredArgsConstructor
public class ComplexCacheConsumer {

    private final ComplexCacheRepository complexCacheRepository;
    private final ObjectMapper objectMapper;

    // 단지 이벤트 수신 — CREATED/UPDATED/DEACTIVATED 모두 처리
    @Transactional
    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "auth-service")
    public void consume(String message) {
        try {
            // 공통 envelope 역직렬화
            EventEnvelope<ApartmentComplexEventPayload> envelope = objectMapper.readValue(
                    message,
                    new TypeReference<>() {}
            );

            EventType eventType = envelope.getEventType();
            ApartmentComplexEventPayload payload = envelope.getPayload();

            if (eventType == EventType.APARTMENT_COMPLEX_CREATED) {
                // 신규 단지 저장
                complexCacheRepository.save(ComplexCache.builder()
                        .id(payload.getApartmentComplexId())
                        .name(payload.getName())
                        .address(payload.getAddress())
                        .status(ComplexCacheStatus.ACTIVE)
                        .build());

            } else if (eventType == EventType.APARTMENT_COMPLEX_UPDATED
                    || eventType == EventType.APARTMENT_COMPLEX_DEACTIVATED) {
                // 기존 캐시 조회 후 갱신
                complexCacheRepository.findById(payload.getApartmentComplexId())
                        .ifPresentOrElse(cache -> {
                            ComplexCacheStatus status = eventType == EventType.APARTMENT_COMPLEX_DEACTIVATED
                                    ? ComplexCacheStatus.INACTIVE
                                    : ComplexCacheStatus.ACTIVE;
                            cache.apply(payload.getName(), payload.getAddress(), status);
                        }, () -> log.warn("complex_cache 없음 — complexId={}", payload.getApartmentComplexId()));
            }

        } catch (Exception e) {
            // 역직렬화 실패 등 예외는 로그만 남기고 오프셋은 넘긴다 (재처리 안 함)
            log.error("ComplexCacheConsumer 처리 실패 — message={}", message, e);
        }
    }
}