package com.apten.parkingvehicle.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.ParkingLogCreateReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogListReq;
import com.apten.parkingvehicle.application.model.request.ParkingStatisticsReq;
import com.apten.parkingvehicle.application.model.request.ParkingZoneListReq;
import com.apten.parkingvehicle.application.model.request.ParkingZonePatchReq;
import com.apten.parkingvehicle.application.model.request.ParkingZonePostReq;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.ParkingLogCreateRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogListRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatisticsRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatusRes;
import com.apten.parkingvehicle.application.model.response.ParkingZoneListRes;
import com.apten.parkingvehicle.application.model.response.ParkingZonePatchRes;
import com.apten.parkingvehicle.application.model.response.ParkingZonePostRes;
import com.apten.parkingvehicle.application.model.response.ResidentParkingStatusRes;
import com.apten.parkingvehicle.domain.entity.ParkingSetting;
import com.apten.parkingvehicle.domain.enums.*;
import com.apten.parkingvehicle.domain.repository.ParkingLogRepository;
import com.apten.parkingvehicle.domain.repository.ParkingSettingRepository;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.apten.parkingvehicle.domain.entity.ParkingLog;
import com.apten.parkingvehicle.domain.entity.ParkingZone;
import com.apten.parkingvehicle.domain.entity.RegularVisitorVehicle;
import com.apten.parkingvehicle.domain.entity.Vehicle;
import com.apten.parkingvehicle.domain.entity.VisitorVehicle;
import com.apten.parkingvehicle.domain.repository.*;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 주차 구역, 입출차, 통계 API를 담당하는 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class ParkingService {

    // 단지별 기능 활성 여부를 검증하는 서비스
    private final FeatureAccessService featureAccessService;

    // 주차 구역 원본 테이블 저장소
    private final ParkingZoneRepository parkingZoneRepository;

    // 입출차 로그 저장소 (직전 IN/OUT 조회, 신규 로그 저장에 사용)
    private final ParkingLogRepository parkingLogRepository;

    // 입주민 차량 저장소 (입차 시 1단계 차종 매칭에 사용)
    private final VehicleRepository vehicleRepository;

    // 방문차량 저장소 (입차 시 2단계 차종 매칭에 사용)
    private final VisitorVehicleRepository visitorVehicleRepository;

    // 고정 방문차량 저장소 (입차 시 3단계 차종 매칭에 사용)
    private final RegularVisitorVehicleRepository regularVisitorVehicleRepository;

    // 단지 주차 운영 타입 확인용 설정 저장소이다.
    private final ParkingSettingRepository parkingSettingRepository;

    // 입출차 기록을 조회한다.
    // 단지 컨텍스트로 범위를 제한하고, 기간/차량번호/입출차/차종 필터를 적용해서 페이지로 반환한다.
    @Transactional(readOnly = true)
    public PageResponse<ParkingLogListRes> getParkingLogList(
            ParkingLogListReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        // 관리자 컨텍스트 해석 + 기능 활성 여부 검증
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 페이지 파라미터 기본값 처리 (null이면 0/20)
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;

        // 날짜 필터를 LocalDateTime 범위로 변환
        // toDate는 그날 23:59:59까지 포함되도록 다음 날 00:00 미만으로 변환한다.
        LocalDateTime fromDateTime = request.getFromDate() != null
                ? request.getFromDate().atStartOfDay()
                : null;
        LocalDateTime toDateTime = request.getToDate() != null
                ? request.getToDate().plusDays(1).atStartOfDay()
                : null;

        // 차량 번호는 공백 제거 후 비어 있으면 null 처리 (LIKE 조건 우회)
        String licensePlate = request.getLicensePlate() != null && !request.getLicensePlate().isBlank()
                ? request.getLicensePlate().trim()
                : null;

        // 차종 분류는 String name으로 변환해서 JPQL 분기 비교에 사용한다.
        String vehicleCategory = request.getVehicleCategory() != null
                ? request.getVehicleCategory().name()
                : null;

        // 페이지 요청 생성 (정렬은 쿼리 ORDER BY loggedAt DESC로 고정되어 있으므로 unsorted)
        Pageable pageable = PageRequest.of(page, size);

        // 동적 필터 페이지 조회
        Page<ParkingLog> resultPage = parkingLogRepository.findFilteredLogs(
                targetComplexId,
                fromDateTime,
                toDateTime,
                request.getEntryType(),
                licensePlate,
                vehicleCategory,
                pageable
        );

        // 빈 결과면 빈 응답 즉시 반환
        if (resultPage.isEmpty()) {
            return PageResponse.empty(page, size);
        }

        // 구역 정보를 일괄 조회해서 zoneId → ParkingZone 맵으로 만들어 둔다 (N+1 방지)
        List<Long> zoneIds = resultPage.getContent().stream()
                .map(ParkingLog::getZoneId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, ParkingZone> zoneMap = zoneIds.isEmpty()
                ? Collections.emptyMap()
                : parkingZoneRepository.findAllById(zoneIds).stream()
                .collect(Collectors.toMap(ParkingZone::getId, z -> z));

        // 응답 DTO로 변환 (차종 분류는 FK 분기로 결정, 구역명은 맵에서 조회)
        List<ParkingLogListRes> content = resultPage.getContent().stream()
                .map(log -> {
                    ParkingZone zone = log.getZoneId() != null ? zoneMap.get(log.getZoneId()) : null;
                    LogVehicleCategory category = resolveLogVehicleCategory(log);
                    return ParkingLogListRes.builder()
                            .parkingLogId(log.getId())
                            .areaName(zone != null ? zone.getAreaName() : null)
                            .zoneName(zone != null ? zone.getZoneName() : null)
                            .licensePlate(log.getLicensePlate())
                            .entryType(log.getEntryType())
                            .vehicleCategory(category)
                            .vehicleCategoryLabel(category.getLabel())
                            .loggedAt(log.getLoggedAt())
                            .memo(log.getMemo())
                            .build();
                })
                .toList();

        // 페이지 메타 정보 포함해서 반환
        return PageResponse.<ParkingLogListRes>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .build();
    }

    // ParkingLog의 FK 패턴으로 차종 분류를 결정한다.
    // vehicle_id, visitor_vehicle_id, regular_visitor_vehicle_id 순서로 확인한다.
    private LogVehicleCategory resolveLogVehicleCategory(ParkingLog log) {
        if (log.getVehicleId() != null) {
            return LogVehicleCategory.RESIDENT;
        }
        if (log.getVisitorVehicleId() != null) {
            return LogVehicleCategory.VISITOR;
        }
        if (log.getRegularVisitorVehicleId() != null) {
            return LogVehicleCategory.REGULAR_VISITOR;
        }
        return LogVehicleCategory.UNREGISTERED;
    }

    // 입출차 로그를 등록한다.
    @Transactional
    public ParkingLogCreateRes createParkingLog(
            ParkingLogCreateReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        // 단지 컨텍스트 해석 + 기능 활성 여부 검증
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 필수값 검증 (zoneId / licensePlate / entryType)
        if (request.getZoneId() == null
                || request.getLicensePlate() == null || request.getLicensePlate().isBlank()
                || request.getEntryType() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 기록 시각이 비어 있으면 서버 현재 시각으로 처리 (자동 게이트 등 시각 미전달 케이스 대응)
        LocalDateTime loggedAt = request.getLoggedAt() != null ? request.getLoggedAt() : LocalDateTime.now();
        String licensePlate = request.getLicensePlate().trim();

        // 주차 구역 검증: 단지 일치 + 활성 상태
        ParkingZone zone = parkingZoneRepository.findByIdAndComplexId(request.getZoneId(), targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));
        if (!Boolean.TRUE.equals(zone.getIsActive())) {
            throw new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_INACTIVE);
        }

        // 직전 로그 조회 (단지 + 차량번호 기준 최신 1건)
        // IN 요청: 중복 IN 차단용 / OUT 요청: 매칭 검증 + 차종 ID 복사용
        ParkingLog lastLog = parkingLogRepository
                .findTopByComplexIdAndLicensePlateOrderByLoggedAtDesc(targetComplexId, licensePlate)
                .orElse(null);

        // 저장할 매칭 ID들 (미매칭은 모두 null로 남김)
        Long matchedVehicleId = null;
        Long matchedVisitorVehicleId = null;
        Long matchedRegularVisitorVehicleId = null;
        Long matchedHouseholdId = null;

        if (request.getEntryType() == ParkingEntryType.IN) {
            // 직전이 IN이면 중복 입차 차단
            if (lastLog != null && lastLog.getEntryType() == ParkingEntryType.IN) {
                throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_IN_ENTRY);
            }

            // 4단계 차종 자동 판별: vehicle → visitor_vehicle → regular_visitor_vehicle → 미등록
            Vehicle vehicle = vehicleRepository
                    .findByComplexIdAndLicensePlateAndStatusAndIsDeletedFalse(
                            targetComplexId, licensePlate, VehicleStatus.APPROVED)
                    .orElse(null);

            if (vehicle != null) {
                // 1단계: 입주민 차량 매칭
                matchedVehicleId = vehicle.getId();
                matchedHouseholdId = vehicle.getHouseholdId();
            } else {
                // 2단계: 방문차량 매칭 (방문 예정일이 입차일과 정확히 일치하는 건만)
                VisitorVehicle visitor = visitorVehicleRepository
                        .findTopByComplexIdAndLicensePlateAndVisitDateAndStatusAndIsDeletedFalseOrderByIdDesc(
                                targetComplexId, licensePlate, loggedAt.toLocalDate(), VisitorVehicleStatus.APPROVED)
                        .orElse(null);

                if (visitor != null) {
                    matchedVisitorVehicleId = visitor.getId();
                    matchedHouseholdId = visitor.getHouseholdId();
                } else {
                    // 3단계: 고정 방문차량 매칭 (시작일 ≤ 입차일 ≤ 종료일 범위 내, 종료일 null 허용)
                    RegularVisitorVehicle regular = regularVisitorVehicleRepository
                            .findActiveByComplexAndPlateAndDate(
                                    targetComplexId, licensePlate, loggedAt.toLocalDate())
                            .orElse(null);

                    if (regular != null) {
                        matchedRegularVisitorVehicleId = regular.getId();
                        matchedHouseholdId = regular.getHouseholdId();
                    }
                    // 4단계: 미등록 차량은 모든 매칭 ID가 null인 채로 기록만 남긴다.
                    // 외부 차량도 잔여 면수 계산에 반영해야 하므로 IN/OUT 자체는 허용한다 (NFR-017).
                }
            }
        } else {
            // OUT 처리
            // 직전 로그가 아예 없으면 입차 기록 없는 출차 시도
            if (lastLog == null) {
                throw new BusinessException(ParkingVehicleErrorCode.NO_IN_ENTRY_FOR_OUT);
            }
            // 직전이 OUT이면 중복 출차 시도
            if (lastLog.getEntryType() == ParkingEntryType.OUT) {
                throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_OUT_ENTRY);
            }

            // OUT은 4단계 재판별을 안 하고 직전 IN의 매칭 정보를 그대로 복사한다.
            // 자정을 넘기거나 방문차량이 EXPIRED로 바뀌어도 IN/OUT이 같은 visit 단위로 묶이게 하기 위함이다.
            matchedVehicleId = lastLog.getVehicleId();
            matchedVisitorVehicleId = lastLog.getVisitorVehicleId();
            matchedRegularVisitorVehicleId = lastLog.getRegularVisitorVehicleId();
            matchedHouseholdId = lastLog.getHouseholdId();
        }

        // parking_log 저장
        ParkingLog log = ParkingLog.builder()
                .complexId(targetComplexId)
                .zoneId(zone.getId())
                .vehicleId(matchedVehicleId)
                .visitorVehicleId(matchedVisitorVehicleId)
                .regularVisitorVehicleId(matchedRegularVisitorVehicleId)
                .householdId(matchedHouseholdId)
                .licensePlate(licensePlate)
                .entryType(request.getEntryType())
                .loggedAt(loggedAt)
                .memo(request.getMemo())
                .build();
        ParkingLog saved = parkingLogRepository.save(log);

        return ParkingLogCreateRes.builder()
                .parkingLogId(saved.getId())
                .zoneId(saved.getZoneId())
                .licensePlate(saved.getLicensePlate())
                .entryType(saved.getEntryType())
                .loggedAt(saved.getLoggedAt())
                .build();
    }

    // 관리자 주차 현황을 조회한다.
    // 단지 주차 운영 타입이 NONE이면 차단하고, BASIC/SENSOR는 활성 구역 기준으로 집계해서 반환한다.
    @Transactional(readOnly = true)
    public ParkingStatusRes getParkingStatus(String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        // 기능이 꺼진 단지는 주차 현황 조회 API 접근을 차단한다.
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 단지 주차 운영 타입 조회 (NONE이면 차단)
        ParkingSetting setting = parkingSettingRepository.findByComplexId(targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SETTING_NOT_FOUND));
        if (setting.getParkingType() == ParkingType.NONE) {
            throw new BusinessException(ParkingVehicleErrorCode.PARKING_TYPE_NONE);
        }

        // 활성 구역만 조회
        List<ParkingZone> activeZones = parkingZoneRepository.findByComplexIdAndIsActiveTrue(targetComplexId);

        // 활성 구역이 없으면 빈 응답으로 즉시 반환
        if (activeZones.isEmpty()) {
            return ParkingStatusRes.builder()
                    .totalSlots(0)
                    .currentParkedCount(0)
                    .remainingSlots(0)
                    .occupancyRate(BigDecimal.ZERO)
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        // 활성 구역 ID 목록 추출 (NOT EXISTS 집계 쿼리 파라미터로 사용)
        List<Long> zoneIds = activeZones.stream()
                .map(ParkingZone::getId)
                .toList();

        // 구역별 현재 입차 차량 수 집계 후 단지 전체 합계 계산
        List<Object[]> rawCounts = parkingLogRepository.countCurrentParkedByZone(targetComplexId, zoneIds);
        int totalParked = 0;
        for (Object[] row : rawCounts) {
            totalParked += ((Number) row[1]).intValue();
        }

        // 활성 구역의 전체 주차 면수 합산
        int totalSlots = activeZones.stream()
                .mapToInt(z -> z.getTotalSlots() != null ? z.getTotalSlots() : 0)
                .sum();

        // 잔여 면수가 음수가 되지 않도록 0으로 하한 처리
        int totalRemaining = Math.max(totalSlots - totalParked, 0);

        // 점유율 계산
        BigDecimal occupancyRate = totalSlots == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(totalParked)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalSlots), 2, RoundingMode.HALF_UP);

        return ParkingStatusRes.builder()
                .totalSlots(totalSlots)
                .currentParkedCount(totalParked)
                .remainingSlots(totalRemaining)
                .occupancyRate(occupancyRate)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 주차 구역 목록을 조회한다.
    @Transactional(readOnly = true)
    public PageResponse<ParkingZoneListRes> getParkingZoneList(
            ParkingZoneListReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        // 단지 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 complexId)
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 단지 기준 주차 구역 전체 조회 (단지 내 zone은 수십 건 수준이라 메모리 필터로 충분)
        List<ParkingZone> zones = parkingZoneRepository.findByComplexId(targetComplexId);

        // 활성 여부 필터 적용
        if (request.getIsActive() != null) {
            zones = zones.stream()
                    .filter(z -> request.getIsActive().equals(z.getIsActive()))
                    .toList();
        }

        // 엔티티 → 응답 DTO 변환
        // currentParkedCount, remainingSlots는 ParkingLog NOT EXISTS 집계 쿼리 필요
        // TODO: getParkingStatus 구현 시 집계 쿼리로 채우기
        List<ParkingZoneListRes> content = zones.stream()
                .map(z -> ParkingZoneListRes.builder()
                        .zoneId(z.getId())
                        .areaName(z.getAreaName())
                        .zoneName(z.getZoneName())
                        .totalSlots(z.getTotalSlots())
                        .currentParkedCount(0)
                        .remainingSlots(z.getTotalSlots())
                        .isActive(z.getIsActive())
                        .build())
                .toList();

        // 단지 내 zone은 페이징 안 하고 한 페이지로 반환
        return PageResponse.<ParkingZoneListRes>builder()
                .content(content)
                .page(0)
                .size(content.size())
                .totalElements(content.size())
                .totalPages(content.isEmpty() ? 0 : 1)
                .hasNext(false)
                .build();
    }

    // 주차 구역을 등록한다.
    @Transactional
    public ParkingZonePostRes createParkingZone(
            ParkingZonePostReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 단지, areaName, zoneName 조합 중복 검증 (zoneName NULL 안전)
        if (parkingZoneRepository.existsByComplexAndAreaAndZoneName(
                targetComplexId, request.getAreaName(), request.getZoneName())) {
            throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_PARKING_ZONE);
        }

        // 엔티티 생성 및 저장 (isActive 미지정 시 true 기본값)
        ParkingZone zone = ParkingZone.builder()
                .complexId(targetComplexId)
                .areaName(request.getAreaName())
                .zoneName(request.getZoneName())
                .totalSlots(request.getTotalSlots() != null ? request.getTotalSlots() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        ParkingZone saved = parkingZoneRepository.save(zone);

        return ParkingZonePostRes.builder()
                .zoneId(saved.getId())
                .areaName(saved.getAreaName())
                .zoneName(saved.getZoneName())
                .totalSlots(saved.getTotalSlots())
                .isActive(saved.getIsActive())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 주차 구역을 수정한다.
    @Transactional
    public ParkingZonePatchRes updateParkingZone(
            Long zoneId,
            ParkingZonePatchReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 단지 일치 + 존재 여부 동시 검증 (다른 단지 zone 접근 차단)
        ParkingZone zone = parkingZoneRepository.findByIdAndComplexId(zoneId, targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));

        // areaName 또는 zoneName이 변경된 경우에만 중복 검증
        boolean areaChanged = !zone.getAreaName().equals(request.getAreaName());
        boolean zoneNameChanged = !Objects.equals(zone.getZoneName(), request.getZoneName());
        if (areaChanged || zoneNameChanged) {
            if (parkingZoneRepository.existsByComplexAndAreaAndZoneName(
                    targetComplexId, request.getAreaName(), request.getZoneName())) {
                throw new BusinessException(ParkingVehicleErrorCode.DUPLICATE_PARKING_ZONE);
            }
        }

        // 도메인 메서드로 수정 (JPA dirty checking으로 자동 반영)
        zone.update(
                request.getAreaName(),
                request.getZoneName(),
                request.getTotalSlots(),
                request.getIsActive()
        );

        return ParkingZonePatchRes.builder()
                .zoneId(zone.getId())
                .areaName(zone.getAreaName())
                .zoneName(zone.getZoneName())
                .totalSlots(zone.getTotalSlots())
                .isActive(zone.getIsActive())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }

    // 주차 구역을 운영 중단 처리한다 (is_active = false).
    // 입출차 로그 보존을 위해 데이터는 유지하며, 운영 중단 zone은 통계 외 화면에서 제외된다.
    @Transactional
    public void deleteParkingZone(
            Long zoneId,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 단지 일치 + 존재 여부 검증
        ParkingZone zone = parkingZoneRepository.findByIdAndComplexId(zoneId, targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_ZONE_NOT_FOUND));

        // 이미 비활성 상태면 멱등 처리 (중복 호출에도 안전)
        if (Boolean.FALSE.equals(zone.getIsActive())) {
            return;
        }

        // TODO: parking_sensor 매핑이 있으면 삭제 차단 (센서 도메인 설계 후 추가)

        // 소프트 삭제 (parking_log FK 보존을 위해 hard delete 안 함)
        zone.deactivate();
    }

    /// 주차 통계를 조회한다.
    // HOURLY: 오늘 0시~24시 24개 슬롯, DAILY: 최근 7일 7개 슬롯.
    // 각 슬롯의 입차/출차 건수 + 슬롯 끝 시점 점유율 단순 평균.
    @Transactional(readOnly = true)
    public ParkingStatisticsRes getParkingStatistics(
            ParkingStatisticsReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 집계 단위가 없으면 기본값 HOURLY
        ParkingStatisticsUnit unit = request.getUnit() != null
                ? request.getUnit()
                : ParkingStatisticsUnit.HOURLY;

        // 기간 결정 (HOURLY는 오늘 하루, DAILY는 최근 7일)
        LocalDate today = LocalDate.now();
        LocalDate fromDate;
        LocalDate toDate;
        if (unit == ParkingStatisticsUnit.HOURLY) {
            fromDate = today;
            toDate = today;
        } else {
            fromDate = today.minusDays(6);
            toDate = today;
        }

        // 기간 시작/끝 시각으로 변환 (끝은 다음 날 00:00 미만으로 포함)
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();

        // 단지 기간 내 모든 로그 한 번에 조회
        List<ParkingLog> logs = parkingLogRepository.findLogsForStatistics(
                targetComplexId, fromDateTime, toDateTime
        );

        // 슬롯별 IN/OUT 건수 + 라벨 + 슬롯 끝 시점 리스트 생성
        List<String> labels;
        List<Integer> inCount;
        List<Integer> outCount;
        List<LocalDateTime> slotEndTimes;

        if (unit == ParkingStatisticsUnit.HOURLY) {
            // 24개 슬롯 (0~23시)
            labels = new java.util.ArrayList<>(24);
            inCount = new java.util.ArrayList<>(24);
            outCount = new java.util.ArrayList<>(24);
            slotEndTimes = new java.util.ArrayList<>(24);
            for (int h = 0; h < 24; h++) {
                labels.add(String.format("%02d시", h));
                inCount.add(0);
                outCount.add(0);
                // 슬롯 끝 시점: 해당 시간대의 마지막 1초 (점유율 계산용)
                slotEndTimes.add(fromDate.atTime(h, 59, 59));
            }
            // 로그를 시간(hour)으로 분류해서 카운트
            for (ParkingLog log : logs) {
                int hour = log.getLoggedAt().getHour();
                if (log.getEntryType() == ParkingEntryType.IN) {
                    inCount.set(hour, inCount.get(hour) + 1);
                } else {
                    outCount.set(hour, outCount.get(hour) + 1);
                }
            }
        } else {
            // 7개 슬롯 (최근 7일)
            labels = new java.util.ArrayList<>(7);
            inCount = new java.util.ArrayList<>(7);
            outCount = new java.util.ArrayList<>(7);
            slotEndTimes = new java.util.ArrayList<>(7);
            // 날짜 -> 슬롯 인덱스 맵으로 빠르게 매칭
            Map<LocalDate, Integer> dateIndexMap = new HashMap<>();
            for (int d = 0; d < 7; d++) {
                LocalDate date = fromDate.plusDays(d);
                labels.add(date.toString().substring(5)); // "MM-DD" 형식
                inCount.add(0);
                outCount.add(0);
                // 슬롯 끝 시점: 그날 23:59:59
                slotEndTimes.add(date.atTime(23, 59, 59));
                dateIndexMap.put(date, d);
            }
            for (ParkingLog log : logs) {
                LocalDate logDate = log.getLoggedAt().toLocalDate();
                Integer idx = dateIndexMap.get(logDate);
                if (idx == null) continue;
                if (log.getEntryType() == ParkingEntryType.IN) {
                    inCount.set(idx, inCount.get(idx) + 1);
                } else {
                    outCount.set(idx, outCount.get(idx) + 1);
                }
            }
        }

        // 평균 점유율 계산 (각 슬롯 끝 시점 점유율의 단순 평균)
        // 활성 zone들의 총 면수를 분모로 사용
        BigDecimal averageOccupancyRate = calculateAverageOccupancyRate(
                targetComplexId, slotEndTimes
        );

        return ParkingStatisticsRes.builder()
                .chartUnit(unit.name())
                .labels(labels)
                .inCount(inCount)
                .outCount(outCount)
                .averageOccupancyRate(averageOccupancyRate)
                .build();
    }

    // 각 시점의 점유율 (현재 입차 수 / 활성 zone 총 면수)을 단순 평균낸다.
    // 활성 zone 총 면수가 0이면 0 반환.
    private BigDecimal calculateAverageOccupancyRate(
            Long complexId,
            List<LocalDateTime> slotEndTimes
    ) {
        // 활성 zone 총 면수 계산 (분모)
        List<ParkingZone> activeZones = parkingZoneRepository.findByComplexIdAndIsActiveTrue(complexId);
        int totalSlots = activeZones.stream()
                .mapToInt(z -> z.getTotalSlots() != null ? z.getTotalSlots() : 0)
                .sum();

        if (totalSlots == 0 || slotEndTimes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 현재 시각 이후의 미래 슬롯은 점유율 계산에서 제외
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> validSlots = slotEndTimes.stream()
                .filter(t -> !t.isAfter(now))
                .toList();

        if (validSlots.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 각 시점 점유율 합산
        BigDecimal totalRate = BigDecimal.ZERO;
        for (LocalDateTime asOf : validSlots) {
            int parked = parkingLogRepository.countParkedAt(complexId, asOf);
            BigDecimal rate = BigDecimal.valueOf(parked)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalSlots), 2, RoundingMode.HALF_UP);
            totalRate = totalRate.add(rate);
        }

        // 평균
        return totalRate.divide(
                BigDecimal.valueOf(validSlots.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    // 입주민 주차 현황을 조회한다.
    // 단지 주차 운영 타입이 NONE이면 차단하고, BASIC/SENSOR는 활성 구역 기준으로 집계해서 반환한다.
    @Transactional(readOnly = true)
    public ResidentParkingStatusRes getResidentParkingStatus(String userRole, Long complexId) {
        // 입주민 컨텍스트 해석 (X-COMPLEX-ID 헤더 기반)
        Long targetComplexId = resolveResidentContextComplexId(userRole, complexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 단지 주차 운영 타입 조회 (NONE이면 차단)
        ParkingSetting setting = parkingSettingRepository.findByComplexId(targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SETTING_NOT_FOUND));
        ParkingType parkingType = setting.getParkingType();
        if (parkingType == ParkingType.NONE) {
            throw new BusinessException(ParkingVehicleErrorCode.PARKING_TYPE_NONE);
        }

        // 활성 구역만 조회 (비활성 구역은 응답에서 제외)
        List<ParkingZone> activeZones = parkingZoneRepository.findByComplexIdAndIsActiveTrue(targetComplexId);

        // 활성 구역이 없으면 빈 응답으로 즉시 반환
        if (activeZones.isEmpty()) {
            return ResidentParkingStatusRes.builder()
                    .parkingTypeCode(parkingType.getCode())
                    .parkingTypeValue(parkingType.getValue())
                    .totalSlots(0)
                    .currentParkedCount(0)
                    .remainingSlots(0)
                    .occupancyRate(BigDecimal.ZERO)
                    .zones(Collections.emptyList())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        // 활성 구역 ID 목록 추출 (NOT EXISTS 집계 쿼리 파라미터로 사용)
        List<Long> zoneIds = activeZones.stream()
                .map(ParkingZone::getId)
                .toList();

        // 구역별 현재 입차 차량 수 집계 (NOT EXISTS 서브쿼리)
        // 결과를 zoneId → count 맵으로 변환해서 zone별 매칭에 사용한다.
        List<Object[]> rawCounts = parkingLogRepository.countCurrentParkedByZone(targetComplexId, zoneIds);
        Map<Long, Integer> parkedCountByZone = new HashMap<>();
        for (Object[] row : rawCounts) {
            Long zoneId = (Long) row[0];
            Integer count = ((Number) row[1]).intValue();
            parkedCountByZone.put(zoneId, count);
        }

        // 구역별 응답 항목 생성 + 전체 집계 누적
        int totalSlots = 0;
        int totalParked = 0;
        List<ResidentParkingStatusRes.ZoneStatus> zoneStatuses = new java.util.ArrayList<>();
        for (ParkingZone zone : activeZones) {
            int zoneTotal = zone.getTotalSlots() != null ? zone.getTotalSlots() : 0;
            int zoneParked = parkedCountByZone.getOrDefault(zone.getId(), 0);
            // 잔여 면수가 음수가 되지 않도록 0으로 하한 처리 (입차 수가 면수를 초과하는 비정상 케이스 방어)
            int zoneRemaining = Math.max(zoneTotal - zoneParked, 0);

            zoneStatuses.add(ResidentParkingStatusRes.ZoneStatus.builder()
                    .zoneId(zone.getId())
                    .areaName(zone.getAreaName())
                    .zoneName(zone.getZoneName())
                    .totalSlots(zoneTotal)
                    .currentParkedCount(zoneParked)
                    .remainingSlots(zoneRemaining)
                    .build());

            totalSlots += zoneTotal;
            totalParked += zoneParked;
        }

        // 단지 전체 잔여 면수와 점유율 계산
        int totalRemaining = Math.max(totalSlots - totalParked, 0);
        BigDecimal occupancyRate = totalSlots == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(totalParked)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalSlots), 2, RoundingMode.HALF_UP);

        return ResidentParkingStatusRes.builder()
                .parkingTypeCode(parkingType.getCode())
                .parkingTypeValue(parkingType.getValue())
                .totalSlots(totalSlots)
                .currentParkedCount(totalParked)
                .remainingSlots(totalRemaining)
                .occupancyRate(occupancyRate)
                .zones(zoneStatuses)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 입주민 주차 API의 단지 컨텍스트를 헤더 기준으로 해석한다.
    // 입주민은 본인 소속 단지 하나만 존재하므로 X-COMPLEX-ID만 검증한다.
    private Long resolveResidentContextComplexId(String userRole, Long complexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (!"USER".equals(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        return complexId;
    }

    // 관리자 주차 API의 단지 컨텍스트를 역할별 헤더 기준으로 해석한다.
    // MASTER는 선택 단지, 일반 관리자는 토큰 단지 기준으로 처리한다.
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
}