package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgramPostReq {

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
}
