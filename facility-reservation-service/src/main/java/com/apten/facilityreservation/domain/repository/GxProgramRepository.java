package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// GX 프로그램 저장/조회 Repository
public interface GxProgramRepository extends JpaRepository<GxProgram, Long> {

    // 승인 리마인더 대상 조회
    List<GxProgram> findByStartDateAndStatusIn(LocalDate startDate, List<GxProgramStatus> statuses);

    // GX 프로그램 조회
    Optional<GxProgram> findByIdAndComplexId(Long id, Long complexId);

    // GX 프로그램 조회 (비관적 락, waitNo 충돌 방지)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM GxProgram g WHERE g.id = :id AND g.complexId = :complexId")
    Optional<GxProgram> findByIdAndComplexIdForUpdate(@Param("id") Long id, @Param("complexId") Long complexId);

    // GX 프로그램 조회 (낙관적 락, 관리자 처리 충돌 감지)
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT g FROM GxProgram g WHERE g.id = :id AND g.complexId = :complexId")
    Optional<GxProgram> findByIdAndComplexIdWithOptimisticLock(@Param("id") Long id, @Param("complexId") Long complexId);

    // GX 프로그램 일괄 조회 (N+1 방지)
    List<GxProgram> findByIdIn(List<Long> ids);

    // GX 월 비용 대상 조회
    List<GxProgram> findByStartDateBetween(LocalDate fromDate, LocalDate toDate);

    // 관리자 GX 프로그램 목록 조회
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

    // 관리자 GX 프로그램 목록 조회 (상태 필터)
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

    // 입주민 GX 프로그램 목록 조회 (취소 제외)
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

    // 입주민 GX 프로그램 목록 조회 (상태 필터)
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
