package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

// 사용자 참조 캐시 저장/조회 Repository
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {
}
