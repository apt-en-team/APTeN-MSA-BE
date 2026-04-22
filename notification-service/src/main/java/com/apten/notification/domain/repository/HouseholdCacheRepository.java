package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.HouseholdCache;
import org.springframework.data.jpa.repository.JpaRepository;

// notification-service의 household cache 저장소
public interface HouseholdCacheRepository extends JpaRepository<HouseholdCache, Long> {
}
