package com.apten.household.domain.repository;

import com.apten.household.domain.entity.FacilityUsageSnapshot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 이용 금액 반영용 스냅샷 저장소이다.
public interface FacilityUsageSnapshotRepository extends JpaRepository<FacilityUsageSnapshot, Long> {

    // 세대와 사용일 기준 시설 이용 스냅샷을 조회한다.
    List<FacilityUsageSnapshot> findByHouseholdIdAndUsageDateBetween(Long householdId, LocalDate fromDate, LocalDate toDate);
}
