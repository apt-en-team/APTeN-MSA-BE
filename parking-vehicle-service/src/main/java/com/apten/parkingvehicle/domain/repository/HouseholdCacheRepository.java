package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// parking-vehicle-service의 household cache 저장소
public interface HouseholdCacheRepository extends JpaRepository<HouseholdCache, Long> {

    // 세대주 사용자 식별자로 세대 캐시를 조회한다.
    Optional<HouseholdCache> findByHeadUserId(Long headUserId);
}
