package com.apten.household.domain.repository;

import com.apten.household.domain.entity.ExpectedResident;
import com.apten.household.domain.enums.ExpectedResidentStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 관리자 입주민 명부 저장소이다.
public interface ExpectedResidentRepository extends JpaRepository<ExpectedResident, Long> {

    List<ExpectedResident> findByComplexIdAndBuildingAndUnitAndStatus(
            Long complexId,
            String building,
            String unit,
            ExpectedResidentStatus status
    );

    List<ExpectedResident> findByComplexIdAndBuildingAndUnitAndStatusNot(
            Long complexId,
            String building,
            String unit,
            ExpectedResidentStatus status
    );

    List<ExpectedResident> findByHouseholdIdAndStatus(Long householdId, ExpectedResidentStatus status);

    List<ExpectedResident> findByHouseholdIdAndStatusNot(Long householdId, ExpectedResidentStatus status);

    List<ExpectedResident> findByHouseholdIdInAndStatusNot(List<Long> householdIds, ExpectedResidentStatus status);

    List<ExpectedResident> findByHouseholdId(Long householdId);

    Page<ExpectedResident> findByComplexId(Long complexId, Pageable pageable);

    Page<ExpectedResident> findByComplexIdAndStatus(
            Long complexId,
            ExpectedResidentStatus status,
            Pageable pageable
    );

    Page<ExpectedResident> findByComplexIdAndStatusNot(
            Long complexId,
            ExpectedResidentStatus status,
            Pageable pageable
    );

    Page<ExpectedResident> findByComplexIdAndHouseholdId(
            Long complexId,
            Long householdId,
            Pageable pageable
    );

    Page<ExpectedResident> findByComplexIdAndHouseholdIdAndStatus(
            Long complexId,
            Long householdId,
            ExpectedResidentStatus status,
            Pageable pageable
    );

    Page<ExpectedResident> findByComplexIdAndHouseholdIdAndStatusNot(
            Long complexId,
            Long householdId,
            ExpectedResidentStatus status,
            Pageable pageable
    );

    // 이번 달 입주 카드는 등록입주민 명부의 실제 입주일 기준으로 세대를 집계한다.
    @Query("""
            SELECT COUNT(DISTINCT e.householdId) FROM ExpectedResident e
            WHERE e.complexId = :complexId
              AND e.status <> :excludedStatus
              AND e.moveInDate >= :from
              AND e.moveInDate < :to
            """)
    long countDistinctHouseholdsByMoveInDateBetween(
            @Param("complexId") Long complexId,
            @Param("excludedStatus") ExpectedResidentStatus excludedStatus,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
            SELECT DISTINCT e.householdId FROM ExpectedResident e
            WHERE e.status = :status
              AND e.moveInDate <= :today
            """)
    List<Long> findDueHouseholdIds(
            @Param("status") ExpectedResidentStatus status,
            @Param("today") LocalDate today
    );
}
