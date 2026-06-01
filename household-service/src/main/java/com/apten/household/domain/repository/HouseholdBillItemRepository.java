package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdBillItem;
import com.apten.household.domain.enums.HouseholdBillItemType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdBillItemRepository extends JpaRepository<HouseholdBillItem, Long> {

    List<HouseholdBillItem> findByBillId(Long billId);

    Optional<HouseholdBillItem> findByBillIdAndItemType(Long billId, HouseholdBillItemType itemType);
}
