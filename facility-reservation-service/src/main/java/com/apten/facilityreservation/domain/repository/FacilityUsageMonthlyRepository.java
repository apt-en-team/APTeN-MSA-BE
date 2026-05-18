package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityUsageMonthly;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대별 월 시설 이용 비용 집계 저장소이다.
public interface FacilityUsageMonthlyRepository extends JpaRepository<FacilityUsageMonthly, Long> {

    // 세대별 월 집계를 조회한다.
    Optional<FacilityUsageMonthly> findByHouseholdIdAndUsageYearAndUsageMonth(Long householdId, Integer usageYear, Integer usageMonth);

    // 단지별 월 집계 목록을 조회한다.
    List<FacilityUsageMonthly> findByComplexIdAndUsageYearAndUsageMonth(Long complexId, Integer usageYear, Integer usageMonth);

    // 월 집계 upsert는 세대별 기존 row를 한 번에 읽어야 N+1을 피할 수 있다.
    List<FacilityUsageMonthly> findByHouseholdIdInAndUsageYearAndUsageMonth(
            List<Long> householdIds,
            Integer usageYear,
            Integer usageMonth
    );

    // 미발행 집계 목록을 조회한다.
    List<FacilityUsageMonthly> findByUsageYearAndUsageMonthAndIsPublishedFalse(Integer usageYear, Integer usageMonth);
}
