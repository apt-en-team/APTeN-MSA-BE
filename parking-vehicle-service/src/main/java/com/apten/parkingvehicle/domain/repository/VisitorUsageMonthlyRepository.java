package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.VisitorUsageMonthly;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 방문차량 월별 이용시간과 비용 집계 저장소이다.
public interface VisitorUsageMonthlyRepository extends JpaRepository<VisitorUsageMonthly, Long> {

    // 세대와 사용 연월 기준 월 집계를 조회한다.
    Optional<VisitorUsageMonthly> findByHouseholdIdAndUsageYearAndUsageMonth(Long householdId, Integer usageYear, Integer usageMonth);

    // 단지와 사용 연월 기준 월 집계 목록을 조회한다.
    List<VisitorUsageMonthly> findByComplexIdAndUsageYearAndUsageMonth(Long complexId, Integer usageYear, Integer usageMonth);
}
