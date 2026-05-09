package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대 상태 변경 이력 저장소이다.
public interface HouseholdHistoryRepository extends JpaRepository<HouseholdHistory, Long> {

    // 세대 ID 기준 이력을 최신순으로 조회한다.
    List<HouseholdHistory> findByHouseholdIdOrderByChangedAtDesc(Long householdId);
}
