package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

// facility-reservation-service의 user cache 저장소
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {
}
