package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대원 저장과 조회를 담당하는 저장소이다.
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {

    // 세대 ID 기준 세대원 목록을 조회한다.
    List<HouseholdMember> findByHouseholdId(Long householdId);

    // 세대 내 활성 세대주를 조회한다.
    Optional<HouseholdMember> findByHouseholdIdAndRoleAndIsActiveTrue(Long householdId, com.apten.household.domain.enums.HouseholdMemberRole role);
}
