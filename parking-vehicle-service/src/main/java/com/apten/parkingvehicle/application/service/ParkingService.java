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
import com.apten.parkingvehicle.domain.repository.VisitorPolicyRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 주차 로그와 현황, 통계 조회를 담당하는 응용 서비스
@RequiredArgsConstructor
@Service
public class ParkingService {

    // 방문차량 정책 원본 저장소이다.
    private final VisitorPolicyRepository visitorPolicyRepository;

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
        // 현재는 단지별 1건 정책 구조이므로 첫 번째 활성 정책을 요약값으로 사용한다.
        // 추후 로그인 관리자 단지 컨텍스트가 생기면 complexId 기준 조회로 바꾼다.
        VisitorPolicySummaryRes summary = visitorPolicyRepository.findAll().stream()
                .findFirst()
                .map(policy -> new VisitorPolicySummaryRes(
                        policy.getFreeMinutes(),
                        policy.getHourFee(),
                        policy.getMonthlyLimitHours(),
                        policy.getIsActive(),
                        LocalDateTime.now()
                ))
                .orElseGet(() -> new VisitorPolicySummaryRes(0, BigDecimal.ZERO, 300, false, LocalDateTime.now()));

        return VisitorVehiclePolicySummaryRes.builder()
                .freeMinutes(summary.freeMinutes())
                .hourFee(summary.hourFee())
                .monthlyLimitHours(summary.monthlyLimitHours())
                .isActive(summary.isActive())
                .updatedAt(summary.updatedAt())
                .build();
    }

    // 방문차량 정책 요약 응답을 내부에서 간단히 옮길 때 사용하는 보조 record이다.
    private record VisitorPolicySummaryRes(
            Integer freeMinutes,
            BigDecimal hourFee,
            Integer monthlyLimitHours,
            Boolean isActive,
            LocalDateTime updatedAt
    ) {
    }
}
