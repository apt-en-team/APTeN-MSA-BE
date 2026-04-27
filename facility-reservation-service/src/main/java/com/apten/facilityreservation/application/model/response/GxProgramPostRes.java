package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// GX 프로그램 등록 응답 DTO이다.
@Getter
@Builder
public class GxProgramPostRes {

    // 프로그램 ID이다.
    private Long programId;

    // 프로그램명이다.
    private String name;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
