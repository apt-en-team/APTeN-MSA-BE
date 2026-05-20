package com.apten.parkingvehicle.infrastructure.sse;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// 단지별 SSE Emitter를 인메모리로 보관하는 레지스트리
@Slf4j
@Component
public class SseEmitterRegistry {

    // 단지별 emitter 보관소 (외부 키 complexId, 내부 키 emitterId)
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

    // 신규 emitter 등록
    public String register(Long complexId, SseEmitter emitter) {
        String emitterId = UUID.randomUUID().toString();
        emitters.computeIfAbsent(complexId, k -> new ConcurrentHashMap<>())
                .put(emitterId, emitter);
        log.debug("SSE 구독 등록 complexId={}, emitterId={}", complexId, emitterId);
        return emitterId;
    }

    // emitter 제거 (compute로 키 단위 atomic 처리)
    // compute는 register의 computeIfAbsent와 동일한 키 lock을 공유한다.
    // 내부 맵 remove, isEmpty 검사, 외부 맵 키 제거를 한 람다 안에서 모두 수행해 race condition을 차단한다.
    public void remove(Long complexId, String emitterId) {
        emitters.compute(complexId, (key, innerMap) -> {
            if (innerMap == null) {
                return null;
            }
            innerMap.remove(emitterId);
            return innerMap.isEmpty() ? null : innerMap;
        });
        log.debug("SSE 구독 해제 complexId={}, emitterId={}", complexId, emitterId);
    }

    // 단지별 emitter 조회 (없으면 빈 맵)
    public Map<String, SseEmitter> findByComplexId(Long complexId) {
        Map<String, SseEmitter> innerMap = emitters.get(complexId);
        if (innerMap == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(innerMap);
    }

    // 전체 emitter 조회 (heartbeat용)
    public Map<Long, Map<String, SseEmitter>> findAll() {
        return Collections.unmodifiableMap(emitters);
    }
}
