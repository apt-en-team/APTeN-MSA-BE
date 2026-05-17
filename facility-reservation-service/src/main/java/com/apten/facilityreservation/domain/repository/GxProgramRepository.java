package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// GX 프로그램 저장소이다.
public interface GxProgramRepository extends JpaRepository<GxProgram, Long> {

    // 단지와 상태 기준 GX 프로그램 목록을 조회한다.
    List<GxProgram> findByComplexIdAndStatus(Long complexId, GxProgramStatus status);

    // 단지 소속 단건 조회한다.
    Optional<GxProgram> findByIdAndComplexId(Long id, Long complexId);

    // 관리자 GX 프로그램 목록 조회 - status 필터 없음
    @Query("""
        SELECT g FROM GxProgram g
        WHERE g.complexId = :complexId
          AND (:facilityId IS NULL OR g.facilityId = :facilityId)
          AND (:fromDate IS NULL OR g.endDate >= :fromDate)
          AND (:toDate IS NULL OR g.startDate <= :toDate)
        ORDER BY g.startDate DESC
        """)
    Page<GxProgram> findAdminGxPrograms(
            @Param("complexId") Long complexId,
            @Param("facilityId") Long facilityId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    // 관리자 GX 프로그램 목록 조회 - status 필터 포함 (enum 파라미터 null 비교 회피)
    @Query("""
        SELECT g FROM GxProgram g
        WHERE g.complexId = :complexId
          AND g.status = :status
          AND (:facilityId IS NULL OR g.facilityId = :facilityId)
          AND (:fromDate IS NULL OR g.endDate >= :fromDate)
          AND (:toDate IS NULL OR g.startDate <= :toDate)
        ORDER BY g.startDate DESC
        """)
    Page<GxProgram> findAdminGxProgramsByStatus(
            @Param("complexId") Long complexId,
            @Param("status") GxProgramStatus status,
            @Param("facilityId") Long facilityId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    // 입주민 GX 프로그램 목록 조회 - status 필터 없음, CANCELLED 제외 (enum null 비교 회피)
    @Query("""
        SELECT g FROM GxProgram g
        WHERE g.complexId = :complexId
          AND g.status <> :cancelled
          AND (:fromDate IS NULL OR g.endDate >= :fromDate)
          AND (:toDate IS NULL OR g.startDate <= :toDate)
        ORDER BY g.startDate DESC
        """)
    Page<GxProgram> findResidentGxPrograms(
            @Param("complexId") Long complexId,
            @Param("cancelled") GxProgramStatus cancelled,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    // 입주민 GX 프로그램 목록 조회 - status 필터 포함 (서비스에서 CANCELLED 진입 차단 보장)
    @Query("""
        SELECT g FROM GxProgram g
        WHERE g.complexId = :complexId
          AND g.status = :status
          AND (:fromDate IS NULL OR g.endDate >= :fromDate)
          AND (:toDate IS NULL OR g.startDate <= :toDate)
        ORDER BY g.startDate DESC
        """)
    Page<GxProgram> findResidentGxProgramsByStatus(
            @Param("complexId") Long complexId,
            @Param("status") GxProgramStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}
