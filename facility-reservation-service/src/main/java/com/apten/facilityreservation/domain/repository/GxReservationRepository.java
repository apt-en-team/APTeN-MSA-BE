package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// GX 예약 저장/조회 Repository
public interface GxReservationRepository extends JpaRepository<GxReservation, Long> {

    // 활성 GX 신청 확인 (WAITING/CONFIRMED)
    boolean existsByProgramIdAndUserIdAndStatusIn(Long programId, Long userId, List<GxReservationStatus> statuses);

    // GX 예약 수 조회 (상태)
    long countByProgramIdAndStatus(Long programId, GxReservationStatus status);

    // GX 예약 목록 조회 (프로그램 + 상태)
    List<GxReservation> findByProgramIdAndStatus(Long programId, GxReservationStatus status);

    // GX 대기 목록 조회 (순번)
    List<GxReservation> findByProgramIdAndStatusOrderByWaitNoAsc(Long programId, GxReservationStatus status);

    // 사용자 GX 예약 조회
    Optional<GxReservation> findByProgramIdAndUserId(Long programId, Long userId);

    // 사용자 GX 예약 조회 (소유권 + 단지)
    Optional<GxReservation> findByIdAndUserIdAndComplexId(Long id, Long userId, Long complexId);

    // 관리자 GX 예약 조회 (단지)
    Optional<GxReservation> findByIdAndComplexId(Long id, Long complexId);

    // 사용자 GX 예약 목록 조회
    List<GxReservation> findByUserIdAndComplexId(Long userId, Long complexId);

    // GX 월 비용 대상 예약 조회
    List<GxReservation> findByStatusAndProgramIdIn(
            GxReservationStatus status,
            List<Long> programIds
    );

    // GX 예약 수 일괄 집계 (N+1 방지)
    @Query("SELECT r.programId, COUNT(r) FROM GxReservation r WHERE r.programId IN :programIds AND r.status = :status GROUP BY r.programId")
    List<Object[]> countByProgramIdInAndStatus(@Param("programIds") List<Long> programIds, @Param("status") GxReservationStatus status);

    // GX 예약 통계 조회 (상태)
    long countByComplexIdAndStatus(Long complexId, GxReservationStatus status);

    // GX 예약 통계 조회 (기간)
    long countByComplexIdAndCreatedAtBetween(Long complexId, LocalDateTime from, LocalDateTime to);

    // 관리자 GX 신청자 목록 조회 (신청순)
    List<GxReservation> findByProgramIdOrderByCreatedAtAsc(Long programId);

    // 관리자 GX 신청자 목록 조회 (상태 + 신청순)
    List<GxReservation> findByProgramIdAndStatusOrderByCreatedAtAsc(Long programId, GxReservationStatus status);

    // GX 완료 처리 대상 조회 (WAITING/CONFIRMED)
    @Query("""
        SELECT r FROM GxReservation r
        JOIN GxProgram p ON p.id = r.programId
        WHERE r.status IN :statuses
          AND (
            p.endDate < :currentDate
            OR (p.endDate = :currentDate AND p.endTime <= :currentTime)
          )
        ORDER BY p.endDate ASC, p.endTime ASC
        """)
    List<GxReservation> findCompletableGxReservations(
            @Param("statuses") List<GxReservationStatus> statuses,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime,
            Pageable pageable
    );

    // 관리자 GX 예약 통합 개요 조회
    @Query("""
        SELECT r FROM GxReservation r
        WHERE r.complexId = :complexId
          AND (:facilityId IS NULL OR r.programId IN (
                SELECT p.id FROM GxProgram p WHERE p.facilityId = :facilityId
              ))
        ORDER BY r.createdAt DESC
        """)
    List<GxReservation> findAdminGxReservationsForOverview(
            @Param("complexId") Long complexId,
            @Param("facilityId") Long facilityId
    );

    // 관리자 GX 예약 통합 개요 조회 (상태 필터)
    @Query("""
        SELECT r FROM GxReservation r
        WHERE r.complexId = :complexId
          AND r.status = :status
          AND (:facilityId IS NULL OR r.programId IN (
                SELECT p.id FROM GxProgram p WHERE p.facilityId = :facilityId
              ))
        ORDER BY r.createdAt DESC
        """)
    List<GxReservation> findAdminGxReservationsForOverviewByStatus(
            @Param("complexId") Long complexId,
            @Param("status") GxReservationStatus status,
            @Param("facilityId") Long facilityId
    );
}
