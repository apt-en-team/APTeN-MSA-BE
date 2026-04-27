package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdBillItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대 월 청구 상세 항목 저장소이다.
public interface HouseholdBillItemRepository extends JpaRepository<HouseholdBillItem, Long> {

    // 청구 ID 기준 상세 항목을 조회한다.
    List<HouseholdBillItem> findByBillId(Long billId);
}
