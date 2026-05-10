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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 주차 구역, 입출차, 통계 API를 담당하는 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class ParkingService {

    private final FeatureAccessService featureAccessService;

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
    public PageResponse<ParkingZoneListRes> getParkingZoneList(ParkingZoneListReq request) {
        //TODO 단지 기준 주차 구역 목록 조회
        //TODO 활성 여부 필터 적용
        return PageResponse.<ParkingZoneListRes>builder()
                .content(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .hasNext(false)
                .build();
    }

    // 주차 구역을 등록한다.
    public ParkingZonePostRes createParkingZone(Long complexId, ParkingZonePostReq request) {
        //TODO 관리자 소속 단지 확인
        //TODO areaName + zoneName 조합 중복 여부 확인 (zoneName NULL 안전)
        //TODO 주차 구역 저장
        return ParkingZonePostRes.builder()
                .zoneId(null)
                .areaName(request.getAreaName())
                .zoneName(request.getZoneName())
                .totalSlots(request.getTotalSlots())
                .isActive(request.getIsActive())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 주차 구역을 수정한다.
    public ParkingZonePatchRes updateParkingZone(Long zoneId, Long complexId, ParkingZonePatchReq request) {
        //TODO 관리자 소속 단지 확인
        //TODO 주차 구역 존재 여부 확인
        //TODO areaName 또는 zoneName 변경 시 중복 여부 확인 (zoneName NULL 안전)
        //TODO 주차 구역 기본 정보 수정
        return ParkingZonePatchRes.builder()
                .zoneId(zoneId)
                .areaName(request.getAreaName())
                .zoneName(request.getZoneName())
                .totalSlots(request.getTotalSlots())
                .isActive(request.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
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