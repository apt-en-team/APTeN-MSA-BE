package com.apten.parkingvehicle.application.scheduler;

import com.apten.parkingvehicle.application.service.SensorMockService;
import com.apten.parkingvehicle.domain.enums.SensorStatus;
import com.apten.parkingvehicle.infrastructure.redis.SensorStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Mock 시연용 자동 토글 스케줄러이다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "apten.scheduler.parking-mock.enabled", havingValue = "true")
public class SensorMockScheduler {

    private final SensorStatusRepository sensorStatusRepository;
    private final SensorMockService sensorMockService;

    // 등록 센서 중 무작위 1개 상태 토글
    @Scheduled(fixedDelayString = "${apten.scheduler.parking-mock.fixed-delay-ms}")
    @SchedulerLock(name = "sensor-mock-toggle", lockAtMostFor = "10s", lockAtLeastFor = "1s")
    public void toggleRandomSensor() {
        String sensorCode = sensorStatusRepository.getRandomSensorCode();
        if (sensorCode == null) {
            return;
        }
        SensorStatus next = sensorMockService.toggleSensor(sensorCode);
        log.info("[mock-scheduler] sensorCode={} toggled to={}", sensorCode, next);
    }
}
