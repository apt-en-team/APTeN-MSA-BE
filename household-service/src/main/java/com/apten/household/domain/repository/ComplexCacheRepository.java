package com.apten.household.domain.repository;

import com.apten.household.domain.entity.ComplexCache;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 캐시 존재 여부와 활성 상태 확인에 사용하는 저장소이다.
public interface ComplexCacheRepository extends JpaRepository<ComplexCache, Long> {
}
