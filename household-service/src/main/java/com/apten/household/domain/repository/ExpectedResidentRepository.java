package com.apten.household.domain.repository;

import com.apten.household.domain.entity.ExpectedResident;
import com.apten.household.domain.enums.ExpectedResidentStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 관리자 입주민 명부 저장소이다.
public interface ExpectedResidentRepository extends JpaRepository<ExpectedResident, Long> {

    List<ExpectedResident> findByComplexIdAndBuildingAndUnitAndStatus(
            Long complexId,
            String building,
            String unit,
            ExpectedResidentStatus status
    );

    List<ExpectedResident> findByHouseholdIdAndStatus(Long householdId, ExpectedResidentStatus status);

    @Query("""
            SELECT e FROM ExpectedResident e
            WHERE e.complexId = :complexId
              AND (:status IS NULL OR e.status = :status)
            """)
    Page<ExpectedResident> findByFilters(Long complexId, ExpectedResidentStatus status, Pageable pageable);
}
