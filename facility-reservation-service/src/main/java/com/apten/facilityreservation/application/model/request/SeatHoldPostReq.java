package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 임시 선점 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatHoldPostReq {

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
}
