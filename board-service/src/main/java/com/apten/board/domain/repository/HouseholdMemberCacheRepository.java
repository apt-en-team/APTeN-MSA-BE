package com.apten.board.domain.repository;

import com.apten.board.domain.entity.HouseholdMemberCache;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// board-service의 household member cache 저장소
public interface HouseholdMemberCacheRepository extends JpaRepository<HouseholdMemberCache, Long> {

    // 현재 사용자에게 연결된 활성 세대원 캐시를 찾는다.
    Optional<HouseholdMemberCache> findByUserIdAndIsActiveTrue(Long userId);
}
