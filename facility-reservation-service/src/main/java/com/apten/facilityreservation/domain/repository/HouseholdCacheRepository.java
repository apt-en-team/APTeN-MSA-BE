package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.HouseholdCache;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 세대 캐시 저장/조회 Repository
public interface HouseholdCacheRepository extends JpaRepository<HouseholdCache, Long> {

    // 세대 캐시 조회
    Optional<HouseholdCache> findByHouseholdId(Long householdId);

}
