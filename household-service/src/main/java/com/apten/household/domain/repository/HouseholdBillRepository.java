package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdBill;
import com.apten.household.domain.enums.HouseholdBillStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 세대 월 청구 헤더 저장소이다.
public interface HouseholdBillRepository extends JpaRepository<HouseholdBill, Long> {

    // 세대와 연월 기준 청구 헤더를 조회한다.
    Optional<HouseholdBill> findByHouseholdIdAndBillYearAndBillMonth(Long householdId, Integer billYear, Integer billMonth);

    // 세대 기준 청구 목록을 최신 연월 순으로 조회한다.
    List<HouseholdBill> findByHouseholdIdOrderByBillYearDescBillMonthDesc(Long householdId);

    // 청구 ID와 단지 ID 기준 청구를 조회한다.
    Optional<HouseholdBill> findByIdAndComplexId(Long id, Long complexId);

    // 관리자 조건 기준 청구 목록을 조회한다.
    @Query("""
            SELECT b FROM HouseholdBill b
            JOIN Household h ON b.householdId = h.id
            WHERE b.complexId = :complexId
              AND (:billYear IS NULL OR b.billYear = :billYear)
              AND (:billMonth IS NULL OR b.billMonth = :billMonth)
              AND (:status IS NULL OR b.status = :status)
              AND (:building IS NULL OR h.building = :building)
              AND (:unit IS NULL OR h.unit = :unit)
            ORDER BY b.billYear DESC, b.billMonth DESC, h.building ASC, h.unit ASC
            """)
    Page<HouseholdBill> findAdminBills(
            @Param("complexId") Long complexId,
            @Param("billYear") Integer billYear,
            @Param("billMonth") Integer billMonth,
            @Param("status") HouseholdBillStatus status,
            @Param("building") String building,
            @Param("unit") String unit,
            Pageable pageable
    );

    // 입주민 조건 기준 확정 청구 목록을 조회한다.
    @Query("""
            SELECT b FROM HouseholdBill b
            WHERE b.householdId = :householdId
              AND b.complexId = :complexId
              AND b.status = :status
              AND (:billYear IS NULL OR b.billYear = :billYear)
              AND (:billMonth IS NULL OR b.billMonth = :billMonth)
            ORDER BY b.billYear DESC, b.billMonth DESC
            """)
    Page<HouseholdBill> findResidentBills(
            @Param("householdId") Long householdId,
            @Param("complexId") Long complexId,
            @Param("status") HouseholdBillStatus status,
            @Param("billYear") Integer billYear,
            @Param("billMonth") Integer billMonth,
            Pageable pageable
    );
}
