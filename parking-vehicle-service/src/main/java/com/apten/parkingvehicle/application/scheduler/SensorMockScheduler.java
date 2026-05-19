package com.apten.parkingvehicle.application.scheduler;

import com.apten.parkingvehicle.application.service.SensorMockService;
import com.apten.parkingvehicle.domain.enums.SensorStatus;
import com.apten.parkingvehicle.infrastructure.redis.SensorStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Mock 시연용 자동 토글 스케줄러이다. 단일 인스턴스 가정으로 분산 락 미적용.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "apten.scheduler.parking-mock.enabled", havingValue = "true")
public class SensorMockScheduler {

    private final SensorStatusRepository sensorStatusRepository;
    private final SensorMockService sensorMockService;

    // 등록 센서 중 무작위 1개 상태 토글
    @Scheduled(fixedDelayString = "${apten.scheduler.parking-mock.fixed-delay-ms}")
    public void toggleRandomSensor() {
        String sensorCode = sensorStatusRepository.getRandomSensorCode();
        if (sensorCode == null) {
            return;
        }
        SensorStatus next = sensorMockService.toggleSensor(sensorCode);
        log.info("[mock-scheduler] sensorCode={} toggled to={}", sensorCode, next);
    }
}
