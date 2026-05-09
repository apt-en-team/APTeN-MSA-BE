package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdMember;
import com.apten.household.domain.enums.HouseholdMemberRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대원 저장과 조회를 담당하는 저장소이다.
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {

    // 세대 ID 기준 세대원 목록을 조회한다.
    List<HouseholdMember> findByHouseholdId(Long householdId);

    // 세대와 사용자 조합으로 세대원 존재 여부를 확인한다.
    boolean existsByHouseholdIdAndUserId(Long householdId, Long userId);

    // 세대와 사용자 조합으로 세대원을 조회한다.
    Optional<HouseholdMember> findByHouseholdIdAndUserId(Long householdId, Long userId);

    // 세대와 사용자 조합으로 활성 세대원을 조회한다.
    Optional<HouseholdMember> findByHouseholdIdAndUserIdAndIsActiveTrue(Long householdId, Long userId);

    // 세대 내 활성 세대주를 조회한다.
    Optional<HouseholdMember> findByHouseholdIdAndRoleAndIsActiveTrue(Long householdId, HouseholdMemberRole role);
}
