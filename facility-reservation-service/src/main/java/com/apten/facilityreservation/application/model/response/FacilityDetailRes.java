package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationType;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 시설 상세 응답 DTO이다.
@Getter
@Builder
public class FacilityDetailRes {

    // 시설 ID이다.
    private Long facilityId;

    // 단지 ID이다.
    private Long complexId;

    // 시설 타입 ID이다.
    private Long typeId;

    // 시설명이다.
    private String name;

    // 시설 설명이다.
    private String description;

    // 예약 방식이다.
    private ReservationType reservationType;

    // 최대 인원이다.
    private Integer maxCount;

    // 운영 시작 시간이다.
    private LocalTime openTime;

    // 운영 종료 시간이다.
    private LocalTime closeTime;

    // 시설 override 예약 단위이다.
    private Integer slotMin;

    // 시설 override 기본 요금이다.
    private BigDecimal baseFee;

    // 활성 여부이다.
    private Boolean isActive;
}
