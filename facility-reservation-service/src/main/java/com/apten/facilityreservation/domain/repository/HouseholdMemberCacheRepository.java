package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// facility-reservation-service의 household member cache 저장소
public interface HouseholdMemberCacheRepository extends JpaRepository<HouseholdMemberCache, Long> {

    // 사용자 ID와 활성 상태 기준 현재 세대 소속을 조회한다.
    Optional<HouseholdMemberCache> findByUserIdAndStatus(Long userId, String status);
}
