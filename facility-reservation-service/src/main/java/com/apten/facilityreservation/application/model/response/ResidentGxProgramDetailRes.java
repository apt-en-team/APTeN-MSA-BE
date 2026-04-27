package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 GX 프로그램 상세 응답 DTO이다.
@Getter
@Builder
public class ResidentGxProgramDetailRes {

    // 프로그램 ID이다.
    private Long programId;

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

    // 상태이다.
    private GxProgramStatus status;
}
