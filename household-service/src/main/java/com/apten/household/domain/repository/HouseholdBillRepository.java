package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdBill;
import com.apten.household.domain.enums.HouseholdBillStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HouseholdBillRepository extends JpaRepository<HouseholdBill, Long> {

    Optional<HouseholdBill> findByHouseholdIdAndBillYearAndBillMonth(Long householdId, Integer billYear, Integer billMonth);

    List<HouseholdBill> findByHouseholdIdOrderByBillYearDescBillMonthDesc(Long householdId);

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
            Long complexId,
            Integer billYear,
            Integer billMonth,
            HouseholdBillStatus status,
            String building,
            String unit,
            Pageable pageable
    );

    @Query("""
            SELECT b FROM HouseholdBill b
            WHERE b.householdId = :householdId
              AND b.complexId = :complexId
              AND b.status = :status
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
            Pageable pageable
    );
}
