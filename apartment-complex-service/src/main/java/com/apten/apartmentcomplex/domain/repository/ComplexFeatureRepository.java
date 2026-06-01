package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.ComplexFeature;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// 단지 기능 저장/조회 Repository
public interface ComplexFeatureRepository extends JpaRepository<ComplexFeature, Long> {

    // 단일 단지 기능 조회
    List<ComplexFeature> findByComplex_Id(Long complexId);

    // 복수 단지 기능 조회
    List<ComplexFeature> findByComplex_IdIn(List<Long> complexIds);

}
