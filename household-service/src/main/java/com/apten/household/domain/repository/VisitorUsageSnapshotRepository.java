package com.apten.household.domain.repository;

import com.apten.household.domain.entity.VisitorUsageSnapshot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 방문차량 이용 집계 스냅샷 저장소이다.
public interface VisitorUsageSnapshotRepository extends JpaRepository<VisitorUsageSnapshot, Long> {

    // 세대와 연월 기준 집계 스냅샷을 조회한다.
    Optional<VisitorUsageSnapshot> findByHouseholdIdAndUsageYearAndUsageMonth(Long householdId, Integer usageYear, Integer usageMonth);

    // 단지와 연월 기준 월별 방문차량 스냅샷 목록을 조회한다.
    List<VisitorUsageSnapshot> findByComplexIdAndUsageYearAndUsageMonth(Long complexId, Integer usageYear, Integer usageMonth);
}
