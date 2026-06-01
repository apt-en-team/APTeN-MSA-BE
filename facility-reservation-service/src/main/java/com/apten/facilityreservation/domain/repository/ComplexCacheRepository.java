package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.ComplexCache;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 캐시 저장/조회 Repository
public interface ComplexCacheRepository extends JpaRepository<ComplexCache, Long> {

    // 활성 단지 조회
    Optional<ComplexCache> findByIdAndStatus(Long id, ComplexCacheStatus status);
}
