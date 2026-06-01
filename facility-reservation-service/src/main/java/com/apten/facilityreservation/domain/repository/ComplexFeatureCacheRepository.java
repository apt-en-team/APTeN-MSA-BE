package com.apten.facilityreservation.domain.repository;

import com.apten.common.enums.FeatureCode;
import com.apten.facilityreservation.domain.entity.ComplexFeatureCache;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 기능 캐시 저장/조회 Repository
public interface ComplexFeatureCacheRepository extends JpaRepository<ComplexFeatureCache, Long> {

    // 단지 기능 캐시 조회
    Optional<ComplexFeatureCache> findByComplexIdAndFeatureCode(Long complexId, FeatureCode featureCode);

    // 단지 기능 캐시 목록 조회
    List<ComplexFeatureCache> findByComplexId(Long complexId);
}
