package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.ParkingFloorListReq;
import com.apten.parkingvehicle.application.model.request.ParkingFloorPatchReq;
import com.apten.parkingvehicle.application.model.request.ParkingFloorPostReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogCreateReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogListReq;
import com.apten.parkingvehicle.application.model.request.ParkingStatisticsReq;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.ParkingFloorListRes;
import com.apten.parkingvehicle.application.model.response.ParkingFloorPatchRes;
import com.apten.parkingvehicle.application.model.response.ParkingFloorPostRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogCreateRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogListRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatisticsRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatusRes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 주차층, 입출차, 통계 API를 담당하는 응용 서비스이다.
@Service
public class ParkingService {

    // 입출차 기록을 조회한다.
    public PageResponse<ParkingLogListRes> getParkingLogList(ParkingLogListReq request) {
        //TODO 관리자 소속 단지 확인
        //TODO 기간, 차량번호, 입출차 필터 적용
        //TODO parking_log 목록 조회
        return PageResponse.empty(request.getPage(), request.getSize());
    }

    // 입출차 로그를 등록한다.
    public ParkingLogCreateRes createParkingLog(ParkingLogCreateReq request) {
        //TODO 주차층 활성 상태 확인
        //TODO 차량번호로 입주민 차량/방문차량/고정 방문차량 매칭
        //TODO 동일 차량 중복 IN/OUT 여부 확인
        //TODO parking_log 저장
        //TODO 방문차량 OUT인 경우 이용시간 집계 대상 표시 또는 월집계 TODO 연결
        return ParkingLogCreateRes.builder()
                .parkingLogId(null)
                .parkingFloorId(request.getParkingFloorId())
                .licensePlate(request.getLicensePlate())
                .entryType(request.getEntryType())
                .loggedAt(request.getLoggedAt())
                .build();
    }

    // 주차 현황을 조회한다.
    public ParkingStatusRes getParkingStatus(Long complexId) {
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

    // 주차층 목록을 조회한다.
    public PageResponse<ParkingFloorListRes> getParkingFloorList(ParkingFloorListReq request) {
        //TODO 단지 기준 주차층 목록 조회
        //TODO 활성 여부 필터 적용
        return PageResponse.<ParkingFloorListRes>builder()
                .content(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .hasNext(false)
                .build();
    }

    // 주차층을 등록한다.
    public ParkingFloorPostRes createParkingFloor(Long complexId, ParkingFloorPostReq request) {
        //TODO 관리자 소속 단지 확인
        //TODO floorName 중복 여부 확인
        //TODO 주차층 저장
        return ParkingFloorPostRes.builder()
                .parkingFloorId(null)
                .floorName(request.getFloorName())
                .totalSlots(request.getTotalSlots())
                .isActive(request.getIsActive())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 주차층을 수정한다.
    public ParkingFloorPatchRes updateParkingFloor(Long parkingFloorId, Long complexId, ParkingFloorPatchReq request) {
        //TODO 관리자 소속 단지 확인
        //TODO 주차층 존재 여부 확인
        //TODO floorName 변경 시 중복 여부 확인
        //TODO 주차층 기본 정보 수정
        return ParkingFloorPatchRes.builder()
                .parkingFloorId(parkingFloorId)
                .floorName(request.getFloorName())
                .totalSlots(request.getTotalSlots())
                .isActive(request.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 주차 통계를 조회한다.
    public ParkingStatisticsRes getParkingStatistics(ParkingStatisticsReq request) {
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
}
