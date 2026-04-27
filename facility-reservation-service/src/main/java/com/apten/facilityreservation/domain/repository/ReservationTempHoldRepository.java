package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.ReservationTempHold;
import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 좌석 임시 선점 저장소이다.
public interface ReservationTempHoldRepository extends JpaRepository<ReservationTempHold, Long> {

    // 같은 좌석 시간대의 HOLDING 선점 존재 여부를 확인한다.
    boolean existsByFacilityIdAndSeatIdAndReservationDateAndStartTimeAndEndTimeAndHoldStatus(
            Long facilityId,
            Long seatId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationHoldStatus holdStatus
    );

    // 만료 시각이 지난 HOLDING 선점 목록을 조회한다.
    List<ReservationTempHold> findByExpiresAtBeforeAndHoldStatus(LocalDateTime expiresAt, ReservationHoldStatus holdStatus);

    // 특정 사용자의 HOLDING 선점을 조회한다.
    Optional<ReservationTempHold> findByIdAndUserIdAndHoldStatus(Long id, Long userId, ReservationHoldStatus holdStatus);
}
