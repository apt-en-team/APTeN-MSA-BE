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
import com.apten.parkingvehicle.domain.entity.ParkingZone;
import com.apten.parkingvehicle.domain.repository.ParkingZoneRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 주차 구역, 입출차, 통계 API를 담당하는 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class ParkingService {

    private final FeatureAccessService featureAccessService;
    private final ParkingZoneRepository parkingZoneRepository;

    // 입출차 기록을 조회한다.
    public PageResponse<ParkingLogListRes> getParkingLogList(
            ParkingLogListReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);
        // TODO: 관리자 단지 컨텍스트 기준으로 입출차 기록 조회 범위를 제한한다.
        //TODO 관리자 소속 단지 확인
        //TODO 기간, 차량번호, 입출차 필터 적용
        //TODO parking_log 목록 조회
        return PageResponse.empty(request.getPage(), request.getSize());
    }

    // 입출차 로그를 등록한다.
    public ParkingLogCreateRes createParkingLog(
            ParkingLogCreateReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);
        // TODO: 입출차 등록 시 parkingZone의 complexId와 관리자 단지 컨텍스트 일치 여부를 검증한다.
        //TODO 주차 구역 활성 상태 확인
        //TODO 차량번호로 입주민 차량/방문차량/고정 방문차량 매칭
        //TODO 동일 차량 중복 IN/OUT 여부 확인
        //TODO parking_log 저장
        //TODO 방문차량 OUT인 경우 이용시간 집계 대상 표시 또는 월집계 TODO 연결
        return ParkingLogCreateRes.builder()
                .parkingLogId(null)
                .zoneId(request.getZoneId())
                .licensePlate(request.getLicensePlate())
                .entryType(request.getEntryType())
                .loggedAt(request.getLoggedAt())
                .build();
    }

    // 주차 현황을 조회한다.
    public ParkingStatusRes getParkingStatus(String userRole, Long complexId, Long selectedComplexId) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        // 기능이 꺼진 단지는 주차 현황 조회 API 접근을 차단한다.
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);
        //TODO 관리자 소속 단지 또는 요청 단지 확인
        //TODO 전체 주차 면수와 현재 주차 대수 계산
        //TODO 점유율과 잔여 면수 계산
        return ParkingStatusRes.builder()
                .totalSlots(0)
                .currentParkedCount(0)
                .remainingSlots(0)
                .occupancyRate(BigDecimal.ZERO)
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

    // 주차 구역을 삭제한다 (소프트 삭제, isActive = false 처리).
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

    // 주차 통계를 조회한다.
    public ParkingStatisticsRes getParkingStatistics(
            ParkingStatisticsReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);
        // TODO: 관리자 단지 컨텍스트 기준으로 주차 통계 조회 범위를 제한한다.
        //TODO 관리자 소속 단지 확인
        //TODO 시간대별 또는 일별 입출차 집계
        //TODO 평균 점유율 계산
        return ParkingStatisticsRes.builder()
                .chartUnit(request.getUnit() != null ? request.getUnit().name() : null)
                .labels(List.of())
                .inCount(List.of())
                .outCount(List.of())
                .averageOccupancyRate(BigDecimal.ZERO)
                .build();
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