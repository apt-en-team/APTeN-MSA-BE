package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.ComplexCache;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 캐시 저장소이다.
public interface ComplexCacheRepository extends JpaRepository<ComplexCache, Long> {

    // 활성 단지만 조회할 때 사용한다.
    Optional<ComplexCache> findByIdAndStatus(Long id, ComplexCacheStatus status);
}
