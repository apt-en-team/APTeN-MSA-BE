package com.apten.auth.domain.repository;

import com.apten.auth.domain.entity.ComplexCache;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 캐시 저장소 — apartment-complex 이벤트 수신 시 upsert에 사용한다
public interface ComplexCacheRepository extends JpaRepository<ComplexCache, Long> {
}