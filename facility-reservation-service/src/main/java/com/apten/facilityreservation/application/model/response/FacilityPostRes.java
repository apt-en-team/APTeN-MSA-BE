package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 등록 응답 DTO이다.
@Getter
@Builder
public class FacilityPostRes {

    // 시설 ID이다.
    private Long facilityId;

    // 단지 ID이다.
    private Long complexId;

    // 시설명이다.
    private String name;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
