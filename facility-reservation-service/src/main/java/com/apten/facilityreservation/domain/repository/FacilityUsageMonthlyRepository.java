package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityUsageMonthly;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 월 시설 이용 비용 저장/조회 Repository
public interface FacilityUsageMonthlyRepository extends JpaRepository<FacilityUsageMonthly, Long> {

    // 세대별 월 비용 조회
    Optional<FacilityUsageMonthly> findByHouseholdIdAndUsageYearAndUsageMonth(Long householdId, Integer usageYear, Integer usageMonth);

    // 단지별 월 비용 목록 조회
    List<FacilityUsageMonthly> findByComplexIdAndUsageYearAndUsageMonth(Long complexId, Integer usageYear, Integer usageMonth);

    // 세대별 월 비용 일괄 조회 (N+1 방지)
    List<FacilityUsageMonthly> findByHouseholdIdInAndUsageYearAndUsageMonth(
            List<Long> householdIds,
            Integer usageYear,
            Integer usageMonth
    );

    // 미발행 월 비용 목록 조회
    List<FacilityUsageMonthly> findByUsageYearAndUsageMonthAndIsPublishedFalse(Integer usageYear, Integer usageMonth);
}
