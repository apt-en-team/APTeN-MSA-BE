package com.apten.auth.domain.repository;

import com.apten.auth.domain.entity.ComplexCache;
import org.springframework.data.jpa.repository.JpaRepository;

// auth-service의 단지 캐시 저장소이다.
public interface ComplexCacheRepository extends JpaRepository<ComplexCache, Long> {
}
