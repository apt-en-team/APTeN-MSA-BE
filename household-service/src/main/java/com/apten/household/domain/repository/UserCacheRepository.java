package com.apten.household.domain.repository;

import com.apten.household.domain.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

// 사용자 캐시 저장소이다.
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {
}
