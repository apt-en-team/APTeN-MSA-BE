package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.time.LocalDate;
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

    // 상태이다.
    private GxProgramStatus status;
}
