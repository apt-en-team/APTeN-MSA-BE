package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 내 예약 목록 응답 DTO이다.
@Getter
@Builder
public class MyReservationListRes {

    // 예약 ID이다.
    private Long reservationId;

    // 시설명이다.
    private String facilityName;

    // 예약일이다.
    private LocalDate reservationDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 좌석 번호이다.
    private Integer seatNo;

    // 예약 상태이다.
    private ReservationStatus status;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
