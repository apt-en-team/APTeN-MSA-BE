package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 시설별 기본 정책 원본 저장소이다.
public interface FacilityPolicyRepository extends JpaRepository<FacilityPolicy, Long> {

    // 시설 ID 기준으로 시설 정책을 조회한다.
    Optional<FacilityPolicy> findByFacilityId(Long facilityId);

    // 시설 ID 기준 활성 정책을 조회한다.
    Optional<FacilityPolicy> findByFacilityIdAndIsActiveTrue(Long facilityId);

    // 단지 기준 시설 정책 목록 조회
    @Query("""
        SELECT p
        FROM FacilityPolicy p
        WHERE EXISTS (
            SELECT 1
            FROM Facility f
            WHERE f.id = p.facilityId
              AND f.complexId = :complexId
              AND f.isDeleted = false
        )
          AND (:facilityId IS NULL OR p.facilityId = :facilityId)
        ORDER BY p.facilityId ASC
        """)
    List<FacilityPolicy> findPolicies(
            @Param("complexId") Long complexId,
            @Param("facilityId") Long facilityId
    );
}
