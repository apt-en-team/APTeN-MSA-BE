package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.HouseholdMemberCache;
import org.springframework.data.jpa.repository.JpaRepository;

// notification-service의 household member cache 저장소
public interface HouseholdMemberCacheRepository extends JpaRepository<HouseholdMemberCache, Long> {
}
