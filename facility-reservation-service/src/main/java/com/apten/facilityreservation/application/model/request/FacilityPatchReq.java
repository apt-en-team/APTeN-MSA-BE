package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.ReservationType;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 수정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPatchReq {

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
}
