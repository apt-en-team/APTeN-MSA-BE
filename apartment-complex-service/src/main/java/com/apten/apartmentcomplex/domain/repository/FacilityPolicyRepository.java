package com.apten.apartmentcomplex.domain.repository;

import com.apten.apartmentcomplex.domain.entity.FacilityPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FacilityPolicyRepository extends JpaRepository<FacilityPolicy, Long> {
    // 단지 ID 기준으로 기존 기본 정책을 조회한다
    Optional<FacilityPolicy> findByComplexIdAndFacilityTypeCode(Long complexId, String facilityTypeCode);
}
