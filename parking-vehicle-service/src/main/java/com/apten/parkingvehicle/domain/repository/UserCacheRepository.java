package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

// parking-vehicle-service의 user cache 저장소
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {
}
