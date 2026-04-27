package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 예약 생성 응답 DTO이다.
@Getter
@Builder
public class ReservationPostRes {

    // 예약 ID이다.
    private Long reservationId;

    // 시설 ID이다.
    private Long facilityId;

    // 예약일이다.
    private LocalDate reservationDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 예약 상태이다.
    private ReservationStatus status;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
