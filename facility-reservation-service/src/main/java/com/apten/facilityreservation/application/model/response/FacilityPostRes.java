package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 등록 응답 DTO이다.
@Getter
@Builder
public class FacilityPostRes {

    // 시설 ID이다.
    private Long facilityId;

    // 시설명이다.
    private String name;

    // 예약 방식이다.
    private ReservationType reservationType;

    // 활성 여부이다.
    private Boolean isActive;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
