package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdBill;
import com.apten.household.domain.enums.HouseholdBillStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HouseholdBillRepository extends JpaRepository<HouseholdBill, Long> {

    Optional<HouseholdBill> findByHouseholdIdAndBillYearAndBillMonth(Long householdId, Integer billYear, Integer billMonth);

    boolean existsByComplexIdAndBillYearAndBillMonth(Long complexId, Integer billYear, Integer billMonth);

    List<HouseholdBill> findBySendDateLessThanEqualAndStatus(LocalDate sendDate, HouseholdBillStatus status);

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
              AND (
                    :building IS NULL
                    OR h.building = :building
                    OR REPLACE(REPLACE(REPLACE(h.building, '동', ''), '호', ''), ' ', '') = :building
                  )
              AND (
                    :unit IS NULL
                    OR h.unit = :unit
                    OR REPLACE(REPLACE(REPLACE(h.unit, '동', ''), '호', ''), ' ', '') = :unit
                  )
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

    // 관리자 조건 기준 청구 건수를 조회한다.
    @Query("""
            SELECT COUNT(b) FROM HouseholdBill b
            JOIN Household h ON b.householdId = h.id
            WHERE b.complexId = :complexId
              AND (:billYear IS NULL OR b.billYear = :billYear)
              AND (:billMonth IS NULL OR b.billMonth = :billMonth)
              AND (:status IS NULL OR b.status = :status)
              AND (
                    :building IS NULL
                    OR h.building = :building
                    OR REPLACE(REPLACE(REPLACE(h.building, '동', ''), '호', ''), ' ', '') = :building
                  )
              AND (
                    :unit IS NULL
                    OR h.unit = :unit
                    OR REPLACE(REPLACE(REPLACE(h.unit, '동', ''), '호', ''), ' ', '') = :unit
                  )
            """)
    Long countAdminBills(
            @Param("complexId") Long complexId,
            @Param("billYear") Integer billYear,
            @Param("billMonth") Integer billMonth,
            @Param("status") HouseholdBillStatus status,
            @Param("building") String building,
            @Param("unit") String unit
    );

    @Query("""
            SELECT b FROM HouseholdBill b
            WHERE b.householdId = :householdId
              AND b.complexId = :complexId
              AND b.status = :status
              AND (b.sendDate IS NULL OR b.sendDate <= :today)
              AND (:billYear IS NULL OR b.billYear = :billYear)
              AND (:billMonth IS NULL OR b.billMonth = :billMonth)
            ORDER BY b.billYear DESC, b.billMonth DESC
            """)
    Page<HouseholdBill> findMyBills(
            Long householdId,
            Long complexId,
            HouseholdBillStatus status,
            Integer billYear,
            Integer billMonth,
            LocalDate today,
            Pageable pageable
    );

    @Query("""
            SELECT b FROM HouseholdBill b
            WHERE b.householdId = :householdId
              AND b.complexId = :complexId
              AND b.status = :status
              AND b.billYear = :billYear
              AND b.billMonth = :billMonth
              AND b.sendDate IS NOT NULL
              AND b.sendDate <= :today
              AND b.homeDisplayUntil IS NOT NULL
              AND b.homeDisplayUntil >= :today
            """)
    Optional<HouseholdBill> findCurrentHomeBill(
            Long householdId,
            Long complexId,
            HouseholdBillStatus status,
            Integer billYear,
            Integer billMonth,
            LocalDate today
    );

    // 특정 세대의 년월 범위 청구 이력을 조회한다.
    @Query("""
            SELECT b FROM HouseholdBill b
            WHERE b.householdId = :householdId
              AND (b.billYear * 100 + b.billMonth) BETWEEN :fromYm AND :toYm
            ORDER BY b.billYear ASC, b.billMonth ASC
            """)
    List<HouseholdBill> findByHouseholdIdAndYearMonthRange(
            @Param("householdId") Long householdId,
            @Param("fromYm") Integer fromYm,
            @Param("toYm") Integer toYm
    );

    // 동일 세대 유형의 확정된 청구 이력을 년월 범위로 조회한다.
    @Query("""
            SELECT b FROM HouseholdBill b
            JOIN Household h ON b.householdId = h.id
            WHERE h.typeId = :typeId
              AND b.complexId = :complexId
              AND b.status = :status
              AND (b.billYear * 100 + b.billMonth) BETWEEN :fromYm AND :toYm
            """)
    List<HouseholdBill> findByTypeIdAndYearMonthRange(
            @Param("typeId") Long typeId,
            @Param("complexId") Long complexId,
            @Param("status") HouseholdBillStatus status,
            @Param("fromYm") Integer fromYm,
            @Param("toYm") Integer toYm
    );

    // 세대 ID 목록 기준 확정된 청구 이력을 년월 범위로 조회한다.
    @Query("""
            SELECT b FROM HouseholdBill b
            WHERE b.householdId IN :householdIds
              AND b.status = :status
              AND (b.billYear * 100 + b.billMonth) BETWEEN :fromYm AND :toYm
            """)
    List<HouseholdBill> findByHouseholdIdsAndYearMonthRange(
            @Param("householdIds") List<Long> householdIds,
            @Param("status") HouseholdBillStatus status,
            @Param("fromYm") Integer fromYm,
            @Param("toYm") Integer toYm
    );
}
