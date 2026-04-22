package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.ComplexCache;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 캐시 저장소
public interface ComplexCacheRepository extends JpaRepository<ComplexCache, Long> {
}
