package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

// 사용자 캐시 저장/조회 Repository
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {

}
