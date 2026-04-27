package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationType;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 시설 목록 응답 DTO이다.
@Getter
@Builder
public class ResidentFacilityListRes {

    // 시설 ID이다.
    private Long facilityId;

    // 시설명이다.
    private String name;

    // 예약 방식이다.
    private ReservationType reservationType;

    // 운영 시작 시간이다.
    private LocalTime openTime;

    // 운영 종료 시간이다.
    private LocalTime closeTime;
}
