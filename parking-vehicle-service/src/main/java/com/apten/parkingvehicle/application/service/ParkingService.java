package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.ParkingFloorSearchReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogPostReq;
import com.apten.parkingvehicle.application.model.request.ParkingLogSearchReq;
import com.apten.parkingvehicle.application.model.request.ParkingStatisticsSearchReq;
import com.apten.parkingvehicle.application.model.response.ParkingFloorRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogGetPageRes;
import com.apten.parkingvehicle.application.model.response.ParkingLogPostRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatisticsRes;
import com.apten.parkingvehicle.application.model.response.ParkingStatusRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePolicySummaryRes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 주차 로그와 현황, 통계 조회를 담당하는 응용 서비스
@Service
public class ParkingService {

    public ParkingLogGetPageRes getParkingLogList(ParkingLogSearchReq request) {
        // TODO: 입출차 기록 조회 로직 구현
        return ParkingLogGetPageRes.empty(request.getPage(), request.getSize());
    }

    public ParkingLogPostRes createParkingLog(ParkingLogPostReq request) {
        // TODO: 입출차 등록 로직 구현
        // TODO: 동일 차량 IN 상태 중복 방지 처리
        return ParkingLogPostRes.builder()
                .floorUid(request.getFloorUid())
                .licensePlate(request.getLicensePlate())
                .entryType(request.getEntryType())
                .loggedAt(request.getLoggedAt())
                .build();
    }

    public ParkingStatusRes getParkingStatus() {
        // TODO: 현재 주차 대수와 잔여 면수 계산 로직 구현
        return ParkingStatusRes.builder()
                .totalSlots(0)
                .currentParkedCount(0)
                .remainingSlots(0)
                .occupancyRate(BigDecimal.ZERO)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public List<ParkingFloorRes> getParkingFloorList(ParkingFloorSearchReq request) {
        // TODO: 층별 주차 현황 조회 로직 구현
        return List.of();
    }

    public ParkingStatisticsRes getParkingStatistics(ParkingStatisticsSearchReq request) {
        // TODO: 시간대별 일별 주차 통계 집계 로직 구현
        return ParkingStatisticsRes.builder()
                .chartUnit(request.getUnit() != null ? request.getUnit().name() : null)
                .labels(List.of())
                .inCount(List.of())
                .outCount(List.of())
                .averageOccupancyRate(BigDecimal.ZERO)
                .build();
    }

    public VisitorVehiclePolicySummaryRes getVisitorVehiclePolicySummary() {
        // TODO: 방문차량 비용 반영 기준 조회 로직 구현
        return VisitorVehiclePolicySummaryRes.builder()
                .freeMinutes(0)
                .hourFee(BigDecimal.ZERO)
                .monthlyLimitHours(0)
                .isActive(false)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
