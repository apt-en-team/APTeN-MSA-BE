package com.apten.parkingvehicle.application.scheduler;

import com.apten.parkingvehicle.infrastructure.sse.SseEmitterRegistry;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// SSE 연결 유지용 keep-alive 발사 스케줄러
@Slf4j
@Component
@RequiredArgsConstructor
public class SseHeartbeatScheduler {

    private final SseEmitterRegistry registry;

    // 모든 emitter에 heartbeat 코멘트 발사
    @Scheduled(fixedDelayString = "${apten.sse.heartbeat-interval-ms:30000}")
    public void sendHeartbeat() {
        for (Map.Entry<Long, Map<String, SseEmitter>> outer : registry.findAll().entrySet()) {
            Long complexId = outer.getKey();
            for (Map.Entry<String, SseEmitter> inner : outer.getValue().entrySet()) {
                String emitterId = inner.getKey();
                SseEmitter emitter = inner.getValue();
                try {
                    emitter.send(SseEmitter.event().comment(" "));
                } catch (IOException e) {
                    // 전송 실패 emitter만 제거하고 나머지 발사는 계속 진행한다.
                    log.warn("SSE heartbeat 실패 complexId={}, emitterId={}", complexId, emitterId, e);
                    registry.remove(complexId, emitterId);
                }
            }
        }
    }
}
