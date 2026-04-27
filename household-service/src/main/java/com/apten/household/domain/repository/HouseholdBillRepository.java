package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdBill;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대 월 청구 헤더 저장소이다.
public interface HouseholdBillRepository extends JpaRepository<HouseholdBill, Long> {

    // 세대와 연월 기준 청구 헤더를 조회한다.
    Optional<HouseholdBill> findByHouseholdIdAndBillYearAndBillMonth(Long householdId, Integer billYear, Integer billMonth);

    // 세대 기준 청구 목록을 최신 연월 순으로 조회한다.
    List<HouseholdBill> findByHouseholdIdOrderByBillYearDescBillMonthDesc(Long householdId);
}
