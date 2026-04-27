package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 일반 예약 저장소이다.
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

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

    // 상태 기준 예약 목록을 조회한다.
    List<Reservation> findByStatus(ReservationStatus status);

    // 예약 ID와 사용자 ID 기준 상세를 조회한다.
    Optional<Reservation> findByIdAndUserId(Long id, Long userId);
}
