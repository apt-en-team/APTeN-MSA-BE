package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.SensorMockPostReq;
import com.apten.parkingvehicle.domain.enums.SensorStatus;
import com.apten.parkingvehicle.infrastructure.redis.SensorStatusRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Mock 시연용 센서 상태 관리 서비스이다.
@Service
@RequiredArgsConstructor
public class SensorMockService {

    private final SensorStatusRepository sensorStatusRepository;

    // 센서 초기 상태를 Redis에 등록한다.
    public void initSensor(SensorMockPostReq request) {
        if (sensorStatusRepository.exists(request.getSensorCode())) {
            throw new IllegalStateException("이미 등록된 센서: " + request.getSensorCode());
        }
        sensorStatusRepository.initSensor(
                request.getSensorCode(),
                request.getZoneId(),
                request.getComplexId(),
                request.getInitialStatus()
        );
    }

    // 센서 상태를 반대 상태로 전환한다.
    public SensorStatus toggleSensor(String sensorCode) {
        if (!sensorStatusRepository.exists(sensorCode)) {
            throw new IllegalStateException("등록되지 않은 센서: " + sensorCode);
        }
        SensorStatus current = sensorStatusRepository.getStatus(sensorCode);
        SensorStatus next = (current == SensorStatus.OCCUPIED) ? SensorStatus.VACANT : SensorStatus.OCCUPIED;
        sensorStatusRepository.updateStatus(sensorCode, next);
        return next;
    }

    // 센서 Hash 전체를 조회한다.
    public Map<String, String> getSensor(String sensorCode) {
        if (!sensorStatusRepository.exists(sensorCode)) {
            throw new IllegalStateException("등록되지 않은 센서: " + sensorCode);
        }
        return sensorStatusRepository.getSensorHash(sensorCode);
    }
}
