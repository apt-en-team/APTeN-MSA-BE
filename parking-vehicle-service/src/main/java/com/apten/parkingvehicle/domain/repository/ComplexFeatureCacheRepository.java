package com.apten.parkingvehicle.domain.repository;

import com.apten.common.enums.FeatureCode;
import com.apten.parkingvehicle.domain.entity.ComplexFeatureCache;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 기능 캐시 저장소이다.
public interface ComplexFeatureCacheRepository extends JpaRepository<ComplexFeatureCache, Long> {

    // 단지와 기능 코드 기준으로 캐시를 조회한다.
    Optional<ComplexFeatureCache> findByComplexIdAndFeatureCode(Long complexId, FeatureCode featureCode);

    // 단지에 속한 전체 기능 캐시를 조회한다.
    List<ComplexFeatureCache> findByComplexId(Long complexId);
}
