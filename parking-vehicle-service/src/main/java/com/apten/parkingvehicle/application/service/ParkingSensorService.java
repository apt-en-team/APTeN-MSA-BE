package com.apten.parkingvehicle.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.event.ParkingSpotChangedEvent;
import com.apten.parkingvehicle.application.model.request.ParkingSensorBulkPostReq;
import com.apten.parkingvehicle.application.model.request.ParkingSensorPatchReq;
import com.apten.parkingvehicle.application.model.request.ParkingSensorPostReq;
import com.apten.parkingvehicle.application.model.response.ParkingSensorBulkPostRes;
import com.apten.parkingvehicle.application.model.response.ParkingSensorRes;
import com.apten.parkingvehicle.domain.entity.ParkingSensor;
import com.apten.parkingvehicle.domain.entity.ParkingZone;
import com.apten.parkingvehicle.domain.enums.SensorStatus;
import com.apten.parkingvehicle.domain.repository.ParkingSensorRepository;
import com.apten.parkingvehicle.domain.repository.ParkingZoneRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import com.apten.parkingvehicle.infrastructure.redis.SensorChangePublisher;
import com.apten.parkingvehicle.infrastructure.redis.SensorStatusRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

// 단지별 주차 센서 CRUD와 일괄 등록을 담당하는 응용 서비스
@Service
@RequiredArgsConstructor
public class ParkingSensorService {

    // 일괄 등록 최대 항목 수
    private static final int BULK_MAX_SIZE = 100;

    // 자리 부가 설명 최대 길이
    private static final int DESCRIPTION_MAX_LENGTH = 100;

    // 센서 코드 최대 길이
    private static final int SENSOR_CODE_MAX_LENGTH = 50;

    // 자리 번호 최대 길이
    private static final int SPOT_NUMBER_MAX_LENGTH = 20;

    // 센서 코드 허용 문자 (영문, 숫자, 하이픈, 언더스코어)
    private static final Pattern SENSOR_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9\\-_]+$");

    // 자리 번호 허용 문자 (\\w + 한글 + 하이픈)
    private static final Pattern SPOT_NUMBER_PATTERN = Pattern.compile("^[\\w가-힣\\-]+$");

    // 주차 센서 저장소
    private final ParkingSensorRepository parkingSensorRepository;

    // 주차 구역 저장소 (zone 검증과 zoneName 매핑에 사용)
    private final ParkingZoneRepository parkingZoneRepository;

    // 단지별 기능 활성 검증 서비스
    private final FeatureAccessService featureAccessService;

    // 자리 변경 SSE 이벤트 발행기
    private final SensorChangePublisher sensorChangePublisher;

    // 자리 점유 상태 Redis 저장소 (status, zoneOccupied 조회)
    private final SensorStatusRepository sensorStatusRepository;

    // 주차 센서 단건 등록
    @Transactional
    public ParkingSensorRes createSensor(
            ParkingSensorPostReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 필수값 null 검증
        if (request == null
                || request.getZoneId() == null
                || request.getSpotNumber() == null
                || request.getSensorCode() == null) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // 포맷과 길이 검증
        validateSensorCodeFormat(request.getSensorCode());
        validateSpotNumberFormat(request.getSpotNumber());
        validateDescriptionLength(request.getDescription());

        // zone 검증 (단지 일치 + 활성)
        ParkingZone zone = validateZone(request.getZoneId(), targetComplexId);

        // 단지 내 sensorCode 중복 차단
        if (parkingSensorRepository.existsByComplexIdAndSensorCodeAndIsDeletedFalse(
                targetComplexId, request.getSensorCode())) {
            throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_SENSOR_CODE);
        }

        // zone 내 spotNumber 중복 차단
        if (parkingSensorRepository.existsByZoneIdAndSpotNumberAndIsDeletedFalse(
                zone.getId(), request.getSpotNumber())) {
            throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_SPOT_NUMBER);
        }

        // 센서 저장
        ParkingSensor saved = parkingSensorRepository.save(
                ParkingSensor.builder()
                        .complexId(targetComplexId)
                        .zoneId(zone.getId())
                        .sensorCode(request.getSensorCode())
                        .spotNumber(request.getSpotNumber())
                        .description(request.getDescription())
                        .build()
        );

        registerSensorRedisInit(List.of(saved), zone);

        return toResponse(saved, zone);
    }

    // 주차 센서 일괄 등록 (한 트랜잭션 안에서 전체 처리)
    @Transactional
    public ParkingSensorBulkPostRes createSensorsBulk(
            ParkingSensorBulkPostReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 요청 자체와 zoneId, items 필수 검증
        if (request == null
                || request.getZoneId() == null
                || request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // 일괄 등록 최대 항목 수 제한
        if (request.getItems().size() > BULK_MAX_SIZE) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // zone 검증 (단지 일치 + 활성)
        ParkingZone zone = validateZone(request.getZoneId(), targetComplexId);

        // 요청 내부 포맷 검증과 sensorCode/spotNumber 중복 사전 차단
        Set<String> sensorCodeSet = new HashSet<>();
        Set<String> spotNumberSet = new HashSet<>();
        for (ParkingSensorBulkPostReq.Item item : request.getItems()) {
            if (item == null
                    || item.getSensorCode() == null
                    || item.getSpotNumber() == null) {
                throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
            }
            validateSensorCodeFormat(item.getSensorCode());
            validateSpotNumberFormat(item.getSpotNumber());
            validateDescriptionLength(item.getDescription());

            if (!sensorCodeSet.add(item.getSensorCode())) {
                throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_IN_REQUEST);
            }
            if (!spotNumberSet.add(item.getSpotNumber())) {
                throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_IN_REQUEST);
            }
        }

        // DB 내 단지 sensorCode 중복 사전 검증
        List<String> existingSensorCodes = parkingSensorRepository
                .findSensorCodesByComplexIdAndSensorCodeInAndIsDeletedFalse(targetComplexId, sensorCodeSet);
        if (!existingSensorCodes.isEmpty()) {
            throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_SENSOR_CODE);
        }

        // DB 내 zone spotNumber 중복 사전 검증
        List<String> existingSpotNumbers = parkingSensorRepository
                .findSpotNumbersByZoneIdAndSpotNumberInAndIsDeletedFalse(zone.getId(), spotNumberSet);
        if (!existingSpotNumbers.isEmpty()) {
            throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_SPOT_NUMBER);
        }

        // 엔티티 변환 후 일괄 저장
        List<ParkingSensor> sensors = new ArrayList<>(request.getItems().size());
        for (ParkingSensorBulkPostReq.Item item : request.getItems()) {
            sensors.add(ParkingSensor.builder()
                    .complexId(targetComplexId)
                    .zoneId(zone.getId())
                    .sensorCode(item.getSensorCode())
                    .spotNumber(item.getSpotNumber())
                    .description(item.getDescription())
                    .build());
        }
        List<ParkingSensor> savedAll = parkingSensorRepository.saveAll(sensors);

        registerSensorRedisInit(savedAll, zone);

        List<Long> createdIds = savedAll.stream()
                .map(ParkingSensor::getId)
                .toList();

        return ParkingSensorBulkPostRes.builder()
                .createdCount(createdIds.size())
                .createdIds(createdIds)
                .build();
    }

    // 주차 구역 기준 센서 목록 조회 (자리 번호 오름차순)
    @Transactional(readOnly = true)
    public List<ParkingSensorRes> getSensorList(
            Long zoneId,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        if (zoneId == null) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // zone 단지 일치만 검증 (비활성 zone도 재활성 전 점검을 위해 목록 노출 허용)
        ParkingZone zone = parkingZoneRepository.findByIdAndComplexId(zoneId, targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));

        List<ParkingSensor> sensors = parkingSensorRepository
                .findByZoneIdAndIsDeletedFalseOrderBySpotNumberAsc(zone.getId());

        return sensors.stream()
                .map(sensor -> toResponse(sensor, zone))
                .toList();
    }

    // 주차 센서 단건 조회
    @Transactional(readOnly = true)
    public ParkingSensorRes getSensor(
            Long sensorId,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        ParkingSensor sensor = findSensorOwnedByComplex(sensorId, targetComplexId);
        ParkingZone zone = parkingZoneRepository.findByIdAndComplexId(sensor.getZoneId(), targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));

        return toResponse(sensor, zone);
    }

    // 주차 센서 수정 (PATCH semantics: null 필드는 변경 안 함)
    @Transactional
    public ParkingSensorRes updateSensor(
            Long sensorId,
            ParkingSensorPatchReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        if (request == null) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // description 길이만 검증 (null이면 변경 안 함)
        validateDescriptionLength(request.getDescription());

        ParkingSensor sensor = findSensorOwnedByComplex(sensorId, targetComplexId);

        ParkingZone zone = parkingZoneRepository.findByIdAndComplexId(sensor.getZoneId(), targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));

        if (request.getDescription() != null) {
            sensor.updateDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            sensor.changeActive(request.getIsActive());
            sensorChangePublisher.publish(buildSensorChangedEvent(sensor, zone));
        }

        return toResponse(sensor, zone);
    }

    // 주차 센서 소프트 삭제 (이미 삭제된 경우 멱등 처리)
    @Transactional
    public void deleteSensor(
            Long sensorId,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        ParkingSensor sensor = findSensorOwnedByComplex(sensorId, targetComplexId);

        if (Boolean.TRUE.equals(sensor.getIsDeleted())) {
            return;
        }
        sensor.markDeleted(LocalDateTime.now());
    }

    // 단지 일치 검증 포함 센서 조회 (다른 단지 센서는 PARKING_SENSOR_NOT_FOUND로 응답)
    private ParkingSensor findSensorOwnedByComplex(Long sensorId, Long complexId) {
        ParkingSensor sensor = parkingSensorRepository.findByIdAndIsDeletedFalse(sensorId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SENSOR_NOT_FOUND));
        if (!sensor.getComplexId().equals(complexId)) {
            throw new BusinessException(ParkingVehicleErrorCode.PARKING_SENSOR_NOT_FOUND);
        }
        return sensor;
    }

    // 단지 일치 + 활성 검증 후 zone 반환
    private ParkingZone validateZone(Long zoneId, Long complexId) {
        ParkingZone zone = parkingZoneRepository.findByIdAndComplexId(zoneId, complexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));
        if (!Boolean.TRUE.equals(zone.getIsActive())) {
            throw new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_INACTIVE);
        }
        return zone;
    }

    // sensorCode 포맷 검증 (null/blank/길이/정규식 위반 시 INVALID_PARAMETER)
    private void validateSensorCodeFormat(String sensorCode) {
        if (sensorCode == null
                || sensorCode.isBlank()
                || sensorCode.length() > SENSOR_CODE_MAX_LENGTH
                || !SENSOR_CODE_PATTERN.matcher(sensorCode).matches()) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }
    }

    // spotNumber 포맷 검증 (null/blank/길이/정규식 위반 시 INVALID_PARAMETER)
    private void validateSpotNumberFormat(String spotNumber) {
        if (spotNumber == null
                || spotNumber.isBlank()
                || spotNumber.length() > SPOT_NUMBER_MAX_LENGTH
                || !SPOT_NUMBER_PATTERN.matcher(spotNumber).matches()) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }
    }

    // description 길이 검증 (null 허용, 초과 시 INVALID_PARAMETER)
    private void validateDescriptionLength(String description) {
        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }
    }

    // 관리자 단지 컨텍스트를 역할별 헤더 기준으로 해석한다 (기존 patterns와 동일)
    private Long resolveAdminContextComplexId(String userRole, Long complexId, Long selectedComplexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if ("MASTER".equals(userRole)) {
            if (selectedComplexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return selectedComplexId;
        }

        if ("MANAGER".equals(userRole) || "ADMIN".equals(userRole)) {
            if (complexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return complexId;
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }

    // ParkingSensor와 ParkingZone을 응답 DTO로 변환
    private ParkingSensorRes toResponse(ParkingSensor sensor, ParkingZone zone) {
        return ParkingSensorRes.builder()
                .sensorId(sensor.getId())
                .zoneId(sensor.getZoneId())
                .zoneName(zone != null ? zone.getZoneName() : null)
                .spotNumber(sensor.getSpotNumber())
                .sensorCode(sensor.getSensorCode())
                .description(sensor.getDescription())
                .isActive(sensor.getIsActive())
                .createdAt(sensor.getCreatedAt())
                .updatedAt(sensor.getUpdatedAt())
                .build();
    }

    // 자리 비활성 변경 SSE 페이로드 빌드
    private ParkingSpotChangedEvent buildSensorChangedEvent(ParkingSensor sensor, ParkingZone zone) {
        return ParkingSpotChangedEvent.builder()
                .complexId(sensor.getComplexId())
                .sensorCode(sensor.getSensorCode())
                .spotNumber(sensor.getSpotNumber())
                .zoneId(sensor.getZoneId())
                .status(sensorStatusRepository.getStatus(sensor.getSensorCode()))
                .isActive(sensor.getIsActive())
                .zoneOccupied(sensorStatusRepository.getZoneOccupied(sensor.getZoneId()).intValue())
                .zoneTotalSlots(zone.getTotalSlots() != null ? zone.getTotalSlots() : 0)
                .changedAt(LocalDateTime.now())
                .build();
    }

    // DB 커밋 후 Redis Hash 초기화 등록 (롤백 시 Redis 미반영)
    private void registerSensorRedisInit(List<ParkingSensor> sensors, ParkingZone zone) {
        Integer zoneTotalSlots = zone.getTotalSlots() != null ? zone.getTotalSlots() : 0;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (ParkingSensor sensor : sensors) {
                    sensorStatusRepository.initSensor(
                            sensor.getSensorCode(),
                            sensor.getZoneId(),
                            sensor.getComplexId(),
                            sensor.getSpotNumber(),
                            zoneTotalSlots,
                            SensorStatus.VACANT
                    );
                }
            }
        });
    }
}
