package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.event.ParkingSpotChangedEvent;
import com.apten.parkingvehicle.application.model.request.SensorMockBulkPostReq;
import com.apten.parkingvehicle.application.model.request.SensorMockPostReq;
import com.apten.parkingvehicle.domain.entity.ParkingSensor;
import com.apten.parkingvehicle.domain.entity.ParkingZone;
import com.apten.parkingvehicle.domain.enums.SensorStatus;
import com.apten.parkingvehicle.domain.repository.ParkingSensorRepository;
import com.apten.parkingvehicle.domain.repository.ParkingZoneRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import com.apten.parkingvehicle.infrastructure.kafka.ParkingVehicleOutboxService;
import com.apten.parkingvehicle.infrastructure.redis.SensorChangePublisher;
import com.apten.parkingvehicle.infrastructure.redis.SensorStatusRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Mock 시연용 센서 상태 관리 서비스이다.
@Service
@RequiredArgsConstructor
public class SensorMockService {

    // 일괄 등록 최대 항목 수
    private static final int BULK_MAX_SIZE = 100;

    // zone 분산락 TTL (ms)
    private static final long ZONE_LOCK_TTL_MS = 3000L;

    // zone 분산락 획득 최대 시도 횟수
    private static final int ZONE_LOCK_MAX_ATTEMPTS = 25;

    // zone 분산락 재시도 대기 (ms)
    private static final long ZONE_LOCK_RETRY_SLEEP_MS = 20L;

    private final SensorStatusRepository sensorStatusRepository;
    private final ParkingSensorRepository parkingSensorRepository;
    private final ParkingZoneRepository parkingZoneRepository;
    private final SensorChangePublisher sensorChangePublisher;
    private final ParkingVehicleOutboxService parkingVehicleOutboxService;

    // 센서 초기 상태를 Redis에 등록한다. DB ParkingSensor와 ParkingZone에서 spotNumber와 zoneTotalSlots를 보강한다.
    public void initSensor(SensorMockPostReq request) {
        // DB ParkingSensor 단건 조회로 spotNumber 보강
        ParkingSensor sensor = parkingSensorRepository
                .findByComplexIdAndSensorCodeAndIsDeletedFalse(request.getComplexId(), request.getSensorCode())
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SENSOR_NOT_FOUND));

        // 요청 zoneId와 DB zoneId 일관성 검증. 불일치 시 Mock 데이터 오염을 차단한다.
        if (!sensor.getZoneId().equals(request.getZoneId())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // DB ParkingZone 단건 조회로 zoneTotalSlots 보강
        ParkingZone zone = parkingZoneRepository
                .findByIdAndComplexId(request.getZoneId(), request.getComplexId())
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));

        sensorStatusRepository.initSensor(
                request.getSensorCode(),
                request.getZoneId(),
                request.getComplexId(),
                sensor.getSpotNumber(),
                zone.getTotalSlots(),
                request.getInitialStatus()
        );
    }

    // 센서 초기 상태 Redis 일괄 등록 (멱등 — 같은 sensorCode 재호출 시 카운터와 Hash 최종 상태가 1회 호출과 동일)
    public void initSensorBulk(SensorMockBulkPostReq request) {
        // 요청 자체와 zoneId, complexId, items 필수 검증
        if (request == null
                || request.getZoneId() == null
                || request.getComplexId() == null
                || request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 일괄 등록 최대 항목 수 제한
        if (request.getItems().size() > BULK_MAX_SIZE) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // DB ParkingZone 단건 조회로 zoneTotalSlots 보강. 루프 밖 1회 수행
        ParkingZone zone = parkingZoneRepository
                .findByIdAndComplexId(request.getZoneId(), request.getComplexId())
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));

        // 1단계 - 전 항목 검증 (요청 내부 sensorCode 중복, DB 존재, zoneId 일치). 한 항목이라도 실패 시 Redis 0건 등록
        Set<String> sensorCodeSet = new HashSet<>();
        Map<String, ParkingSensor> validatedSensors = new LinkedHashMap<>();
        for (SensorMockBulkPostReq.Item item : request.getItems()) {
            if (item == null
                    || item.getSensorCode() == null
                    || item.getInitialStatus() == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }

            // 요청 내부 sensorCode 중복 차단
            if (!sensorCodeSet.add(item.getSensorCode())) {
                throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_IN_REQUEST);
            }

            // DB ParkingSensor 단건 조회로 spotNumber 보강
            ParkingSensor sensor = parkingSensorRepository
                    .findByComplexIdAndSensorCodeAndIsDeletedFalse(request.getComplexId(), item.getSensorCode())
                    .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SENSOR_NOT_FOUND));

            // 요청 zoneId와 DB zoneId 일관성 검증. 불일치 시 Mock 데이터 오염 차단
            if (!sensor.getZoneId().equals(request.getZoneId())) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }

            validatedSensors.put(item.getSensorCode(), sensor);
        }

        // 2단계 - 검증 통과 항목 Redis 등록 (멱등이라 exists 사전 차단 없음)
        for (SensorMockBulkPostReq.Item item : request.getItems()) {
            ParkingSensor sensor = validatedSensors.get(item.getSensorCode());
            sensorStatusRepository.initSensor(
                    item.getSensorCode(),
                    request.getZoneId(),
                    request.getComplexId(),
                    sensor.getSpotNumber(),
                    zone.getTotalSlots(),
                    item.getInitialStatus()
            );
        }
    }

    // 센서 상태를 반대 상태로 전환한다. 같은 zone 동시 토글을 분산락으로 직렬화해 카운터 경합을 차단한다.
    public SensorStatus toggleSensor(String sensorCode) {
        if (!sensorStatusRepository.exists(sensorCode)) {
            throw new IllegalStateException("등록되지 않은 센서: " + sensorCode);
        }

        // zone/complex 식별자는 락 키와 활성 판정에 필요하므로 Hash에서 먼저 확보한다
        Map<String, String> hash = sensorStatusRepository.getSensorHash(sensorCode);
        Long zoneId = Long.valueOf(hash.get(SensorStatusRepository.FIELD_ZONE_ID));
        Long complexId = Long.valueOf(hash.get(SensorStatusRepository.FIELD_COMPLEX_ID));

        String token = UUID.randomUUID().toString();
        // 락 경합 시 상태 변경 없이 현재 상태 반환 — 재동기화 스케줄러가 카운터 정합을 유지한다
        if (!acquireZoneLock(zoneId, token)) {
            return sensorStatusRepository.getStatus(sensorCode);
        }

        try {
            SensorStatus current = sensorStatusRepository.getStatus(sensorCode);
            SensorStatus next = (current == SensorStatus.OCCUPIED) ? SensorStatus.VACANT : SensorStatus.OCCUPIED;

            // 사용불가 자리는 카운터에 영향을 주지 않도록 활성 여부를 먼저 조회한다
            boolean affectCounter = Boolean.TRUE.equals(resolveSensorActive(complexId, sensorCode));
            boolean changed = sensorStatusRepository.updateStatus(sensorCode, next, affectCounter);

            // 실제 전이가 일어난 경우에만 최신 Hash와 zone 카운터로 페이로드를 구성해 발행한다.
            if (changed) {
                ParkingSpotChangedEvent event = buildSpotChangedEvent(sensorCode);
                if (event != null) {
                    sensorChangePublisher.publish(event);
                    parkingVehicleOutboxService.saveParkingSpotChangedEvent(event);
                }
            }
            return next;
        } finally {
            sensorStatusRepository.unlockZone(zoneId, token);
        }
    }

    // zone 분산락을 짧게 재시도하며 획득한다.
    private boolean acquireZoneLock(Long zoneId, String token) {
        for (int attempt = 0; attempt < ZONE_LOCK_MAX_ATTEMPTS; attempt++) {
            if (sensorStatusRepository.tryLockZone(zoneId, token, ZONE_LOCK_TTL_MS)) {
                return true;
            }
            try {
                Thread.sleep(ZONE_LOCK_RETRY_SLEEP_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    // 센서 Hash 전체를 조회한다.
    public Map<String, String> getSensor(String sensorCode) {
        if (!sensorStatusRepository.exists(sensorCode)) {
            throw new IllegalStateException("등록되지 않은 센서: " + sensorCode);
        }
        return sensorStatusRepository.getSensorHash(sensorCode);
    }

    // Hash와 zone 카운터로부터 SSE 페이로드를 구성한다. Hash가 비어 있으면 null을 반환한다.
    private ParkingSpotChangedEvent buildSpotChangedEvent(String sensorCode) {
        Map<String, String> hash = sensorStatusRepository.getSensorHash(sensorCode);
        if (hash.isEmpty()) {
            return null;
        }
        Long complexId = Long.valueOf(hash.get(SensorStatusRepository.FIELD_COMPLEX_ID));
        Long zoneId = Long.valueOf(hash.get(SensorStatusRepository.FIELD_ZONE_ID));
        return ParkingSpotChangedEvent.builder()
                .complexId(complexId)
                .sensorCode(sensorCode)
                .spotNumber(hash.get(SensorStatusRepository.FIELD_SPOT_NUMBER))
                .zoneId(zoneId)
                .status(SensorStatus.valueOf(hash.get(SensorStatusRepository.FIELD_STATUS)))
                .isActive(resolveSensorActive(complexId, sensorCode))
                .zoneOccupied(sensorStatusRepository.getZoneOccupied(zoneId).intValue())
                .zoneTotalSlots(Optional.ofNullable(hash.get(SensorStatusRepository.FIELD_ZONE_TOTAL_SLOTS))
                        .filter(v -> !v.equals("null")).map(Integer::parseInt).orElse(0))
                .changedAt(LocalDateTime.parse(hash.get(SensorStatusRepository.FIELD_CHANGED_AT)))
                .build();
    }

    // 자리 활성 여부 조회 — parking_sensor.is_active 가 유일한 원본
    private Boolean resolveSensorActive(Long complexId, String sensorCode) {
        return parkingSensorRepository
                .findByComplexIdAndSensorCodeAndIsDeletedFalse(complexId, sensorCode)
                .map(ParkingSensor::getIsActive)
                .orElse(null);
    }
}
