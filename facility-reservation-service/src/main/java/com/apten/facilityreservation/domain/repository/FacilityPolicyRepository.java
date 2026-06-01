package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 시설 정책 저장/조회 Repository
public interface FacilityPolicyRepository extends JpaRepository<FacilityPolicy, Long> {

    // 시설 정책 조회
    Optional<FacilityPolicy> findByComplexIdAndFacilityId(Long complexId, Long facilityId);

    // 활성 시설 정책 조회
    Optional<FacilityPolicy> findByComplexIdAndFacilityIdAndIsActiveTrue(Long complexId, Long facilityId);

    // 활성 시설 정책 일괄 조회 (N+1 방지)
    List<FacilityPolicy> findByComplexIdAndFacilityIdInAndIsActiveTrue(Long complexId, List<Long> facilityIds);

    // 단지 기준 시설 정책 목록 조회
    @Query("""
        SELECT p
        FROM FacilityPolicy p
        WHERE p.complexId = :complexId
          AND (:facilityId IS NULL OR p.facilityId = :facilityId)
        ORDER BY p.facilityId ASC
        """)
    List<FacilityPolicy> findPolicies(
            @Param("complexId") Long complexId,
            @Param("facilityId") Long facilityId
    );
}
