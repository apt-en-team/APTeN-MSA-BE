package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 내 예약 상세 응답 DTO이다.
@Getter
@Builder
public class MyReservationDetailRes {

    // 예약 ID이다.
    private Long reservationId;

    // 시설 ID이다.
    private Long facilityId;

    // 시설명이다.
    private String facilityName;

    // 좌석 번호이다.
    private Integer seatNo;

    // 예약일이다.
    private LocalDate reservationDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 예약 상태이다.
    private ReservationStatus status;

    // 취소 가능 여부이다.
    private Boolean cancelable;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
