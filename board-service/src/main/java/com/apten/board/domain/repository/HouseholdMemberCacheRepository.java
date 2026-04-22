package com.apten.board.domain.repository;

import com.apten.board.domain.entity.HouseholdMemberCache;
import org.springframework.data.jpa.repository.JpaRepository;

// board-service의 household member cache 저장소
public interface HouseholdMemberCacheRepository extends JpaRepository<HouseholdMemberCache, Long> {
}
