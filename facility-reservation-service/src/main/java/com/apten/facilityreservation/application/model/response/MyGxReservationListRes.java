package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 내 GX 예약 목록 응답 DTO이다.
@Getter
@Builder
public class MyGxReservationListRes {

    // GX 예약 ID이다.
    private Long gxReservationId;

    // 프로그램 ID이다.
    private Long programId;

    // 프로그램명이다.
    private String programName;

    // 시작일이다.
    private LocalDate startDate;

    // 종료일이다.
    private LocalDate endDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 운영 요일이다.
    private String daysOfWeek;

    // 수강료이다.
    private BigDecimal baseFee;

    // GX 예약 상태이다.
    private GxReservationStatus status;

    // 대기 순번이다.
    private Integer waitNo;

    // 프로그램 상태이다.
    private GxProgramStatus programStatus;
}
