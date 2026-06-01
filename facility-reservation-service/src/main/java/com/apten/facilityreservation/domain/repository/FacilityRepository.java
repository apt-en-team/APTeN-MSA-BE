package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.Facility;
import java.util.List;
import java.util.Optional;

import com.apten.facilityreservation.domain.enums.ReservationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 시설 저장/조회 Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // 활성 시설 목록 조회
    java.util.List<Facility> findByComplexIdAndIsDeletedFalse(Long complexId);

    // 시설 상세 조회 (삭제 제외)
    Optional<Facility> findByIdAndComplexIdAndIsDeletedFalse(Long id, Long complexId);

    // 시설 조회 (삭제 제외)
    Optional<Facility> findByIdAndIsDeletedFalse(Long id);

    // 입주민 시설 목록 조회 (활성 + 삭제 제외)
    @Query("""
        SELECT f FROM Facility f
        WHERE f.complexId = :complexId
          AND f.isDeleted = false
          AND f.isActive = true
          AND (:typeId IS NULL OR f.typeId = :typeId)
        ORDER BY f.createdAt DESC
        """)
    List<Facility> findResidentFacilities(
            @Param("complexId") Long complexId,
            @Param("typeId") Long typeId
    );

    // 관리자 시설 목록 조회
    @Query("""
        SELECT f
        FROM Facility f
        WHERE f.complexId = :complexId
          AND f.isDeleted = false
          AND (:typeId IS NULL OR f.typeId = :typeId)
          AND (:isActive IS NULL OR f.isActive = :isActive)
        ORDER BY f.createdAt DESC
        """)
    Page<Facility> findAdminFacilities(
            @Param("complexId") Long complexId,
            @Param("typeId") Long typeId,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    // 관리자 시설 목록 조회 (예약 방식 필터)
    @Query("""
        SELECT f
        FROM Facility f
        WHERE f.complexId = :complexId
          AND f.isDeleted = false
          AND (:typeId IS NULL OR f.typeId = :typeId)
          AND f.reservationType = :reservationType
          AND (:isActive IS NULL OR f.isActive = :isActive)
        ORDER BY f.createdAt DESC
        """)
    Page<Facility> findAdminFacilitiesByReservationType(
            @Param("complexId") Long complexId,
            @Param("typeId") Long typeId,
            @Param("reservationType") ReservationType reservationType,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
