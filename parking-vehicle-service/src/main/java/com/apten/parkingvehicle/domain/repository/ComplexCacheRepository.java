package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ComplexCache;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 기본 정보 캐시 저장소이다.
public interface ComplexCacheRepository extends JpaRepository<ComplexCache, Long> {

    // 단지 캐시를 식별자로 조회한다.
    Optional<ComplexCache> findById(Long id);
}
