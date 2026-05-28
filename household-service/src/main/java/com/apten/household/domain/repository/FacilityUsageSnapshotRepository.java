package com.apten.household.domain.repository;

import com.apten.household.domain.entity.FacilityUsageSnapshot;
import com.apten.household.domain.enums.FacilityUsageStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityUsageSnapshotRepository extends JpaRepository<FacilityUsageSnapshot, Long> {

    List<FacilityUsageSnapshot> findByHouseholdIdAndUsageDateBetween(Long householdId, LocalDate fromDate, LocalDate toDate);

    List<FacilityUsageSnapshot> findByComplexIdAndUsageDateBetweenAndStatus(
            Long complexId,
            LocalDate fromDate,
            LocalDate toDate,
            FacilityUsageStatus status
    );
}
