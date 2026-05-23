package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.event.ParkingSpotChangedEvent;
import com.apten.parkingvehicle.application.model.event.ZoneCounterChangedEvent;
import com.apten.parkingvehicle.infrastructure.sse.SseEmitterRegistry;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// 입주민 SSE 구독 시작과 자리 변경 이벤트 발행을 담당하는 응용 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class SseSubscribeService {

    private final SseEmitterRegistry registry;
    private final ParkingSettingService parkingSettingService;

    // SSE 연결 유지 timeout 값 (ms)
    @Value("${apten.sse.timeout-ms:1800000}")
    private long timeoutMs;

    // 입주민 SSE 구독 시작
    public SseEmitter subscribe(String userRole, Long complexId) {
        validateResidentContext(userRole, complexId);
        parkingSettingService.validateParkingEnabled(complexId);

        SseEmitter emitter = new SseEmitter(timeoutMs);
        String emitterId = registry.register(complexId, emitter);

        // 콜백 3종에 동일하게 registry.remove 등록한다. 연결 종료/타임아웃/에러 모두 같은 정리 흐름이다.
        emitter.onCompletion(() -> registry.remove(complexId, emitterId));
        emitter.onTimeout(() -> registry.remove(complexId, emitterId));
        emitter.onError(t -> registry.remove(complexId, emitterId));

        return emitter;
    }

    // 단지별 emitter에 자리 변경 이벤트 발행
    // 호출자는 3단계 Redis Pub/Sub 리스너에서 연결 예정. 지금은 호출되는 곳이 없다.
    public void publishToComplex(Long complexId, ParkingSpotChangedEvent event) {
        Map<String, SseEmitter> targets = registry.findByComplexId(complexId);
        for (Map.Entry<String, SseEmitter> entry : targets.entrySet()) {
            String emitterId = entry.getKey();
            SseEmitter emitter = entry.getValue();
            try {
                emitter.send(SseEmitter.event()
                        .name("spot-changed")
                        .data(event));
            } catch (IOException e) {
                // 전송 실패 emitter만 제거하고 다른 emitter 발송은 계속 진행한다.
                log.warn("SSE 전송 실패 complexId={}, emitterId={}", complexId, emitterId, e);
                registry.remove(complexId, emitterId);
            }
        }
    }

    // 단지별 emitter에 zone 카운터 변경 이벤트 발행
    public void publishZoneCounterToComplex(Long complexId, ZoneCounterChangedEvent event) {
        Map<String, SseEmitter> targets = registry.findByComplexId(complexId);
        for (Map.Entry<String, SseEmitter> entry : targets.entrySet()) {
            String emitterId = entry.getKey();
            SseEmitter emitter = entry.getValue();
            try {
                emitter.send(SseEmitter.event()
                        .name("zone-counter-changed")
                        .data(event));
            } catch (IOException e) {
                // 전송 실패 emitter만 제거하고 다른 emitter 발송은 계속 진행한다.
                log.warn("SSE zone 카운터 전송 실패 complexId={}, emitterId={}", complexId, emitterId, e);
                registry.remove(complexId, emitterId);
            }
        }
    }

    // 입주민 단지 컨텍스트 검증
    private void validateResidentContext(String userRole, Long complexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        if (!"USER".equals(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
