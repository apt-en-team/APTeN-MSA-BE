package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationType;
import lombok.Builder;
import lombok.Getter;

// 관리자 시설 목록 응답 DTO이다.
@Getter
@Builder
public class FacilityListRes {

    // 시설 ID이다.
    private Long facilityId;

    // 시설 타입 ID이다.
    private Long typeId;

    // 시설명이다.
    private String name;

    // 예약 방식이다.
    private ReservationType reservationType;

    // 활성 여부이다.
    private Boolean isActive;
}
