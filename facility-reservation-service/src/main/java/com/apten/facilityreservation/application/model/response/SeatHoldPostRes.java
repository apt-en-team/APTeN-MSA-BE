package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 좌석 임시 선점 응답 DTO이다.
@Getter
@Builder
public class SeatHoldPostRes {

    // 선점 ID이다.
    private Long holdId;

    // 시설 ID이다.
    private Long facilityId;

    // 좌석 ID이다.
    private Long seatId;

    // 예약일이다.
    private LocalDate reservationDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 선점 상태이다.
    private ReservationHoldStatus status;

    // 만료 시각이다.
    private LocalDateTime expiresAt;
}
