package com.apten.household.domain.repository;

import com.apten.household.domain.entity.HouseholdMember;
import com.apten.household.domain.enums.HouseholdMemberRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 세대원 저장과 조회를 담당하는 Repository이다.
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {

    // 세대 ID 기준 세대원 목록을 조회한다.
    List<HouseholdMember> findByHouseholdId(Long householdId);

    // 세대와 사용자 조합으로 세대원 존재 여부를 확인한다.
    long countByHouseholdIdAndIsActiveTrue(Long householdId);

    @Query("""
            SELECT m.householdId AS householdId, COUNT(m) AS memberCount
            FROM HouseholdMember m
            WHERE m.householdId IN :householdIds
              AND m.isActive = true
            GROUP BY m.householdId
            """)
    List<HouseholdMemberCountProjection> countActiveMembersByHouseholdIds(@Param("householdIds") Collection<Long> householdIds);

    boolean existsByHouseholdIdAndUserId(Long householdId, Long userId);

    // 세대와 사용자 조합으로 세대원을 조회한다.
    Optional<HouseholdMember> findByHouseholdIdAndUserId(Long householdId, Long userId);

    // 세대와 사용자 조합으로 활성 세대원을 조회한다.
    Optional<HouseholdMember> findByHouseholdIdAndUserIdAndIsActiveTrue(Long householdId, Long userId);

    // 세대 안의 활성 세대주를 조회한다.
    Optional<HouseholdMember> findByHouseholdIdAndRoleAndIsActiveTrue(Long householdId, HouseholdMemberRole role);

    // 사용자가 해당 단지에 활성 세대원으로 등록되어 있는지 확인한다.
    @Query("SELECT COUNT(m) > 0 FROM HouseholdMember m JOIN Household h ON m.householdId = h.id WHERE m.userId = :userId AND h.complexId = :complexId AND m.isActive = true")
    boolean existsByUserIdAndComplexId(Long userId, Long complexId);

    // 사용자와 단지 기준 활성 세대원을 조회한다.
    @Query("SELECT m FROM HouseholdMember m JOIN Household h ON m.householdId = h.id WHERE m.userId = :userId AND h.complexId = :complexId AND m.isActive = true")
    Optional<HouseholdMember> findActiveByUserIdAndComplexId(Long userId, Long complexId);

    List<HouseholdMember> findAllByUserIdAndIsActiveTrue(Long userId);

    // 세대원 ID가 해당 단지 소속 세대에 속하는지 조회한다.
    @Query("SELECT m FROM HouseholdMember m JOIN Household h ON m.householdId = h.id WHERE m.id = :householdMemberId AND h.complexId = :complexId")
    Optional<HouseholdMember> findByIdAndComplexId(Long householdMemberId, Long complexId);
}
