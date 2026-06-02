package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대원 캐시 저장/조회 Repository
public interface HouseholdMemberCacheRepository extends JpaRepository<HouseholdMemberCache, Long> {

    // 활성 세대원 캐시 조회
    Optional<HouseholdMemberCache> findByUserIdAndStatus(Long userId, String status);

    // 세대원 캐시 목록 조회
    List<HouseholdMemberCache> findByHouseholdId(Long householdId);

}
