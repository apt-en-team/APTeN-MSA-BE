package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

// notification-service의 user cache 저장소
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {
}
