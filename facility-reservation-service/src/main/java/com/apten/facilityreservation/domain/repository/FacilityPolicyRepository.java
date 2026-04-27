package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 시설 타입 기본 정책 원본 저장소이다.
public interface FacilityPolicyRepository extends JpaRepository<FacilityPolicy, Long> {

    // 단지 ID와 시설 타입 코드 기준으로 시설 정책을 조회한다.
    Optional<FacilityPolicy> findByComplexIdAndFacilityTypeCode(Long complexId, String facilityTypeCode);
}
