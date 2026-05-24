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

// GX 예약 저장소이다.
public interface GxReservationRepository extends JpaRepository<GxReservation, Long> {

    // 같은 사용자 중복 신청 여부를 확인한다.
    boolean existsByProgramIdAndUserId(Long programId, Long userId);

    // 상태 기준 예약 수를 센다.
    long countByProgramIdAndStatus(Long programId, GxReservationStatus status);

    // 프로그램과 상태 기준 예약 목록을 조회한다.
    List<GxReservation> findByProgramIdAndStatus(Long programId, GxReservationStatus status);

    // 대기 순번 기준으로 예약 목록을 조회한다.
    List<GxReservation> findByProgramIdAndStatusOrderByWaitNoAsc(Long programId, GxReservationStatus status);

    // 사용자의 GX 예약을 조회한다.
    Optional<GxReservation> findByProgramIdAndUserId(Long programId, Long userId);

    // userId와 complexId 기준 소유권+단지 범위를 동시에 검증하는 단건 조회이다.
    Optional<GxReservation> findByIdAndUserIdAndComplexId(Long id, Long userId, Long complexId);

    // 관리자 단지 범위 단건 조회이다.
    Optional<GxReservation> findByIdAndComplexId(Long id, Long complexId);

    // 사용자의 단지 내 전체 GX 예약 목록을 조회한다.
    List<GxReservation> findByUserIdAndComplexId(Long userId, Long complexId);

    // GX 비용 산정: 프로그램 시작일 기준으로 확정된 예약을 조회한다.
    List<GxReservation> findByStatusAndProgramIdIn(
            GxReservationStatus status,
            List<Long> programIds
    );

    // 이전 방식 — approvedAt 기준 조회 (레거시, 현재 미사용)
    List<GxReservation> findByStatusAndApprovedAtBetween(
            GxReservationStatus status,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    );

    // 복수 프로그램의 상태별 예약 수를 집계한다. (N+1 방지)
    @Query("SELECT r.programId, COUNT(r) FROM GxReservation r WHERE r.programId IN :programIds AND r.status = :status GROUP BY r.programId")
    List<Object[]> countByProgramIdInAndStatus(@Param("programIds") List<Long> programIds, @Param("status") GxReservationStatus status);

    // 통계 — 단지 기준 상태별 GX 예약 수
    long countByComplexIdAndStatus(Long complexId, GxReservationStatus status);

    // 통계 — 단지 기준 기간별 GX 예약 생성 수
    long countByComplexIdAndCreatedAtBetween(Long complexId, LocalDateTime from, LocalDateTime to);

    // 관리자 GX 프로그램 신청자 전체 목록 조회 — 신청 시각 오름차순
    List<GxReservation> findByProgramIdOrderByCreatedAtAsc(Long programId);

    // 관리자 GX 프로그램 신청자 상태별 목록 조회 — 신청 시각 오름차순
    List<GxReservation> findByProgramIdAndStatusOrderByCreatedAtAsc(Long programId, GxReservationStatus status);

    // 이용완료 처리 대상 조회 — GxProgram 종료 시각이 지난 WAITING/CONFIRMED 예약을 대상으로 한다.
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

    // 관리자 GX 예약 통합 개요 조회 — status 필터 없음
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

    // 관리자 GX 예약 통합 개요 조회 — status 필터 포함 (enum null 비교 회피)
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
