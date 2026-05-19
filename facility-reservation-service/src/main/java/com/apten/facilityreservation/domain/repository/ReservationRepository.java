package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 일반 예약 저장소이다.
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 시설 기준 예약 존재 여부 조회
    boolean existsByFacilityId(Long facilityId);

    // 같은 시간대 예약 존재 여부를 확인한다.
    boolean existsByUserIdAndFacilityIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
            Long userId,
            Long facilityId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    );

    // 같은 시간대 정원형 예약 수를 센다.
    long countByFacilityIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
            Long facilityId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    );

    // 사용자별 예약 목록을 조회한다.
    List<Reservation> findByUserId(Long userId);

    // 시설별 예약 목록을 조회한다.
    List<Reservation> findByFacilityId(Long facilityId);

    // 시설의 미래 확정 예약 존재 여부를 확인한다.
    boolean existsByFacilityIdAndReservationDateGreaterThanEqualAndStatus(
            Long facilityId,
            java.time.LocalDate reservationDate,
            ReservationStatus status
    );

    // 상태 기준 예약 목록을 조회한다.
    List<Reservation> findByStatus(ReservationStatus status);

    // 시설과 날짜, 상태 기준 예약 목록을 조회한다.
    List<Reservation> findByFacilityIdAndReservationDateAndStatus(Long facilityId, LocalDate reservationDate, ReservationStatus status);

    // 상태별 현황 집계는 DB count를 직접 사용해 불필요한 목록 적재를 줄인다.
    long countByFacilityIdAndReservationDateAndStatus(Long facilityId, LocalDate reservationDate, ReservationStatus status);

    // 좌석형 중복 예약 검증에 사용할 예약 목록을 조회한다.
    List<Reservation> findByFacilityIdAndSeatIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    );

    // 좌석형 동일 슬롯의 확정 예약 존재 여부를 빠르게 확인한다.
    boolean existsByFacilityIdAndSeatIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    );

    // 사용자의 동일 시간대 중복 예약 검증에 사용할 예약 목록을 조회한다.
    List<Reservation> findByUserIdAndReservationDateAndStartTimeAndEndTimeAndStatus(
            Long userId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    );

    // 세대별 월 비용 산정용 예약 목록을 조회한다.
    List<Reservation> findByHouseholdIdAndReservationDateBetween(Long householdId, LocalDate fromDate, LocalDate toDate);

    // 세대별 상태 기준 월 비용 산정용 예약 목록을 조회한다.
    List<Reservation> findByHouseholdIdAndStatusAndReservationDateBetween(
            Long householdId,
            ReservationStatus status,
            LocalDate fromDate,
            LocalDate toDate
    );

    // 월 비용 산정은 완료된 이용 실적만 반영해야 하므로 COMPLETED 기준으로 조회한다.
    List<Reservation> findByStatusAndReservationDateBetween(
            ReservationStatus status,
            LocalDate fromDate,
            LocalDate toDate
    );

    // 예약 ID와 사용자 ID 기준 상세를 조회한다.
    Optional<Reservation> findByIdAndUserId(Long id, Long userId);

    // 예약 ID와 단지 ID 기준 상세를 조회한다.
    Optional<Reservation> findByIdAndComplexId(Long id, Long complexId);

    // 관리자 예약 목록 조회 — status 필터 없음 (enum null 비교 회피)
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

    // 관리자 예약 목록 조회 — status 필터 포함 (enum non-null 보장)
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

    // 내 예약 목록 조회 — status 필터 없음 (enum null 비교 회피)
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

    // 내 예약 목록 조회 — status 필터 포함 (enum non-null 보장)
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

    // 완료 처리는 비용/현황 집계의 기준이 되므로 종료 시각이 지난 CONFIRMED만 조회한다.
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.status = :status
          AND (
            r.reservationDate < :currentDate
            OR (r.reservationDate = :currentDate AND r.endTime < :currentTime)
          )
        ORDER BY r.reservationDate ASC, r.endTime ASC
        """)
    List<Reservation> findCompletableReservations(
            @Param("status") ReservationStatus status,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime,
            Pageable pageable
    );

    // 관리자 예약 통합 개요 조회 — status 필터 없음
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

    // 관리자 예약 통합 개요 조회 — status 필터 포함 (enum null 비교 회피)
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
}
