package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.ReservationTempHold;
import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 좌석 임시 선점 저장/조회 Repository
public interface ReservationTempHoldRepository extends JpaRepository<ReservationTempHold, Long> {

    // 유효 좌석 선점 중복 확인
    boolean existsByFacilityIdAndSeatIdAndReservationDateAndStartTimeAndEndTimeAndHoldStatusAndExpiresAtAfter(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationHoldStatus holdStatus,
            LocalDateTime expiresAt
    );

    // 만료 좌석 선점 목록 조회
    List<ReservationTempHold> findByHoldStatusAndExpiresAtLessThanEqual(ReservationHoldStatus holdStatus, LocalDateTime expiresAt);

    // 사용자 좌석 선점 조회
    Optional<ReservationTempHold> findByIdAndUserIdAndHoldStatus(Long id, Long userId, ReservationHoldStatus holdStatus);

    // 유효 좌석 선점 일괄 조회
    List<ReservationTempHold> findByFacilityIdAndReservationDateAndHoldStatusAndExpiresAtAfter(
            Long facilityId,
            LocalDate reservationDate,
            ReservationHoldStatus holdStatus,
            LocalDateTime expiresAt
    );
}
