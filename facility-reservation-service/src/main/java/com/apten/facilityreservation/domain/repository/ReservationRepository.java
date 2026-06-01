package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 일반 예약 저장/조회 Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 시설 기준 예약 존재 여부 조회
    boolean existsByFacilityId(Long facilityId);

    // 시간대 예약 중복 확인
    boolean existsByUserIdAndFacilityIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
            Long userId,
            Long facilityId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    );

    // 시간대 정원형 예약 수 조회
    long countByFacilityIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
            Long facilityId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    );

    // 사용자 예약 목록 조회 (통합 목록)
    List<Reservation> findByUserIdAndComplexId(Long userId, Long complexId);

    // 미래 확정 예약 확인
    boolean existsByFacilityIdAndReservationDateGreaterThanEqualAndStatus(
            Long facilityId,
            java.time.LocalDate reservationDate,
            ReservationStatus status
    );

    // 예약 목록 조회 (시설 + 날짜 + 상태)
    List<Reservation> findByFacilityIdAndReservationDateAndStatus(Long facilityId, LocalDate reservationDate, ReservationStatus status);

    // 예약 목록 조회 (시설 + 날짜 + 복수 상태)
    List<Reservation> findByFacilityIdAndReservationDateAndStatusIn(Long facilityId, LocalDate reservationDate, List<ReservationStatus> statuses);

    // 예약 수 조회 (상태)
    long countByFacilityIdAndReservationDateAndStatus(Long facilityId, LocalDate reservationDate, ReservationStatus status);

    // 좌석형 확정 예약 중복 확인
    boolean existsByFacilityIdAndSeatIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    );

    // 월 비용 대상 예약 조회 (COMPLETED)
    List<Reservation> findByStatusAndReservationDateBetween(
            ReservationStatus status,
            LocalDate fromDate,
            LocalDate toDate
    );

    // 사용자 예약 상세 조회
    Optional<Reservation> findByIdAndUserId(Long id, Long userId);

    // 관리자 예약 상세 조회
    Optional<Reservation> findByIdAndComplexId(Long id, Long complexId);

    // 관리자 예약 목록 조회
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.complexId = :complexId
          AND (:facilityId IS NULL OR r.facilityId = :facilityId)
          AND (:reservationDate IS NULL OR r.reservationDate = :reservationDate)
        ORDER BY r.reservationDate DESC, r.startTime DESC
        """)
    Page<Reservation> findAdminReservations(
            @Param("complexId") Long complexId,
            @Param("facilityId") Long facilityId,
            @Param("reservationDate") LocalDate reservationDate,
            Pageable pageable
    );

    // 관리자 예약 목록 조회 (상태 필터)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.complexId = :complexId
          AND r.status = :status
          AND (:facilityId IS NULL OR r.facilityId = :facilityId)
          AND (:reservationDate IS NULL OR r.reservationDate = :reservationDate)
        ORDER BY r.reservationDate DESC, r.startTime DESC
        """)
    Page<Reservation> findAdminReservationsByStatus(
            @Param("complexId") Long complexId,
            @Param("status") ReservationStatus status,
            @Param("facilityId") Long facilityId,
            @Param("reservationDate") LocalDate reservationDate,
            Pageable pageable
    );

    // 내 예약 목록 조회
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.userId = :userId
          AND r.complexId = :complexId
          AND (:fromDate IS NULL OR r.reservationDate >= :fromDate)
          AND (:toDate IS NULL OR r.reservationDate <= :toDate)
        ORDER BY r.reservationDate DESC, r.startTime DESC
        """)
    Page<Reservation> findMyReservations(
            @Param("userId") Long userId,
            @Param("complexId") Long complexId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    // 내 예약 목록 조회 (상태 필터)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.userId = :userId
          AND r.complexId = :complexId
          AND r.status = :status
          AND (:fromDate IS NULL OR r.reservationDate >= :fromDate)
          AND (:toDate IS NULL OR r.reservationDate <= :toDate)
        ORDER BY r.reservationDate DESC, r.startTime DESC
        """)
    Page<Reservation> findMyReservationsByStatus(
            @Param("userId") Long userId,
            @Param("complexId") Long complexId,
            @Param("status") ReservationStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    // 완료 처리 대상 조회 (야간 예약 익일 완료)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.status = :status
          AND (
            (r.endTime >= r.startTime AND (
              r.reservationDate < :currentDate
              OR (r.reservationDate = :currentDate AND r.endTime <= :currentTime)
            ))
            OR
            (r.endTime < r.startTime AND (
              r.reservationDate < :yesterday
              OR (r.reservationDate = :yesterday AND r.endTime <= :currentTime)
            ))
          )
        ORDER BY r.reservationDate ASC, r.endTime ASC
        """)
    List<Reservation> findCompletableReservations(
            @Param("status") ReservationStatus status,
            @Param("currentDate") LocalDate currentDate,
            @Param("yesterday") LocalDate yesterday,
            @Param("currentTime") LocalTime currentTime,
            Pageable pageable
    );

    // 사용자 시설 예약 확인 (구독 해지 검증)
    boolean existsByUserIdAndFacilityIdAndStatus(Long userId, Long facilityId, ReservationStatus status);

    // 사용자 기간별 시설 예약 확인 (월 이용 이력)
    boolean existsByUserIdAndFacilityIdAndReservationDateBetweenAndStatus(
            Long userId, Long facilityId, LocalDate from, LocalDate to, ReservationStatus status);

    // 예약 통계 조회 (오늘)
    long countByComplexIdAndReservationDate(Long complexId, LocalDate reservationDate);

    // 예약 통계 조회 (날짜 + 상태)
    long countByComplexIdAndReservationDateAndStatus(Long complexId, LocalDate reservationDate, ReservationStatus status);

    // 예약 통계 조회 (상태)
    long countByComplexIdAndStatus(Long complexId, ReservationStatus status);

    // 예약 통계 조회 (기간)
    long countByComplexIdAndCreatedAtBetween(Long complexId, LocalDateTime from, LocalDateTime to);

    // 관리자 예약 통합 개요 조회
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.complexId = :complexId
          AND (:facilityId IS NULL OR r.facilityId = :facilityId)
          AND (:reservationDate IS NULL OR r.reservationDate = :reservationDate)
        ORDER BY r.createdAt DESC
        """)
    List<Reservation> findAdminReservationsForOverview(
            @Param("complexId") Long complexId,
            @Param("facilityId") Long facilityId,
            @Param("reservationDate") LocalDate reservationDate
    );

    // 관리자 예약 통합 개요 조회 (상태 필터)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.complexId = :complexId
          AND r.status = :status
          AND (:facilityId IS NULL OR r.facilityId = :facilityId)
          AND (:reservationDate IS NULL OR r.reservationDate = :reservationDate)
        ORDER BY r.createdAt DESC
        """)
    List<Reservation> findAdminReservationsForOverviewByStatus(
            @Param("complexId") Long complexId,
            @Param("status") ReservationStatus status,
            @Param("facilityId") Long facilityId,
            @Param("reservationDate") LocalDate reservationDate
    );

    // 시설별 예약 수 일괄 집계 (N+1 방지)
    @Query("""
        SELECT r.facilityId, COUNT(r)
        FROM Reservation r
        WHERE r.facilityId IN :facilityIds
          AND r.reservationDate = :date
          AND r.status IN :statuses
        GROUP BY r.facilityId
        """)
    List<Object[]> countGroupByFacilityId(
            @Param("facilityIds") List<Long> facilityIds,
            @Param("date") LocalDate date,
            @Param("statuses") List<ReservationStatus> statuses
    );
}
