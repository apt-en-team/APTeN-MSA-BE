package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// GX 프로그램 상세 응답 DTO이다.
@Getter
@Builder
public class GxProgramDetailRes {

    // 프로그램 ID이다.
    private Long programId;

    // 단지 ID이다.
    private Long complexId;

    // 시설 ID이다.
    private Long facilityId;

    // 프로그램명이다.
    private String name;

    // 설명이다.
    private String description;

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

    // 최대 인원이다.
    private Integer maxCount;

    // 최소 인원이다.
    private Integer minCount;

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

    // 거절 인원이다.
    private Integer rejectedCount;
}
