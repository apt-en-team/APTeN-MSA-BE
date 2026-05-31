package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.HouseholdMemberCache;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// parking-vehicle-service의 household member cache 저장소
public interface HouseholdMemberCacheRepository extends JpaRepository<HouseholdMemberCache, Long> {

    // 사용자 식별자로 활성 세대 매핑 단건을 조회한다.
    Optional<HouseholdMemberCache> findByUserIdAndIsActiveTrue(Long userId);

    // 세대 식별자로 활성 구성원 목록을 조회한다.
    List<HouseholdMemberCache> findByHouseholdIdAndIsActiveTrue(Long householdId);
}
