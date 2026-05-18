package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// GX 프로그램 목록 응답 DTO이다.
@Getter
@Builder
public class GxProgramListRes {

    // 프로그램 ID이다.
    private Long programId;

    // 시설 ID이다.
    private Long facilityId;

    // 프로그램명이다.
    private String name;

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

    // 최소 정원이다.
    private Integer minCount;

    // 최대 정원이다.
    private Integer maxCount;

    // 프로그램별 개별 요금이다.
    private BigDecimal baseFee;

    // 대기 신청 허용 여부이다.
    private Boolean waitingEnabled;

    // 상태이다.
    private GxProgramStatus status;

    // 확정 인원이다.
    private Integer confirmedCount;

    // 대기 인원이다.
    private Integer waitingCount;
}
