package com.apten.board.domain.repository;

import com.apten.board.domain.entity.HouseholdCache;
import org.springframework.data.jpa.repository.JpaRepository;

// board-service의 household cache 저장소
public interface HouseholdCacheRepository extends JpaRepository<HouseholdCache, Long> {
}
