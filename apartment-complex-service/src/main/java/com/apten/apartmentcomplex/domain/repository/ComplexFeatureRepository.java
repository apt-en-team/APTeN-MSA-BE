package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ComplexFeature;
import com.apten.common.enums.FeatureCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 기능 원본 저장과 조회를 담당하는 JPA Repository이다.
public interface ComplexFeatureRepository extends JpaRepository<ComplexFeature, Long> {

    List<ComplexFeature> findByComplex_Id(Long complexId);

    List<ComplexFeature> findByComplex_IdIn(List<Long> complexIds);

    Optional<ComplexFeature> findByComplex_IdAndFeatureCode(Long complexId, FeatureCode featureCode);

    void deleteByComplex_Id(Long complexId);

    boolean existsByComplex_IdAndFeatureCode(Long complexId, FeatureCode featureCode);
}
