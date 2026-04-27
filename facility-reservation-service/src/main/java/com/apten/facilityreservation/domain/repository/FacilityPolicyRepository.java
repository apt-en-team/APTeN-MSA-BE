package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 타입 기본 정책 원본 저장소이다.
public interface FacilityPolicyRepository extends JpaRepository<FacilityPolicy, Long> {

    // 단지 ID와 시설 타입 코드 기준으로 시설 정책을 조회한다.
    Optional<FacilityPolicy> findByComplexIdAndFacilityTypeCode(Long complexId, FacilityTypeCode facilityTypeCode);

    // 단지 ID와 시설 타입 코드 기준 활성 정책을 조회한다.
    Optional<FacilityPolicy> findByComplexIdAndFacilityTypeCodeAndIsActiveTrue(Long complexId, FacilityTypeCode facilityTypeCode);

    // 단지의 정책 목록을 조회한다.
    List<FacilityPolicy> findByComplexId(Long complexId);
}
