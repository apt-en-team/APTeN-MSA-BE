package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.VehicleFeeMonthly;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 차량 월별 비용 집계 저장소이다.
public interface VehicleFeeMonthlyRepository extends JpaRepository<VehicleFeeMonthly, Long> {

    // 세대와 청구 연월 기준 차량 비용 월집계를 조회한다.
    Optional<VehicleFeeMonthly> findByHouseholdIdAndBillYearAndBillMonth(Long householdId, Integer billYear, Integer billMonth);

    // 단지와 청구 연월 기준 차량 비용 월집계 목록을 조회한다.
    List<VehicleFeeMonthly> findByComplexIdAndBillYearAndBillMonth(Long complexId, Integer billYear, Integer billMonth);
}
