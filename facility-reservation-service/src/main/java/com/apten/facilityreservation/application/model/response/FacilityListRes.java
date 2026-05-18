package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityUsageUnitType;
import com.apten.facilityreservation.domain.enums.ReservationType;
import java.math.BigDecimal;
import java.time.LocalTime;
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

    // 운영 시작 시간이다.
    private LocalTime openTime;

    // 운영 종료 시간이다.
    private LocalTime closeTime;

    // 활성 여부이다.
    private Boolean isActive;

    // 기본 요금이다. 정책 미등록 시 null.
    private BigDecimal baseFee;

    // 예약 슬롯 단위(분)이다. 정책 미등록 시 null.
    private Integer slotMin;

    // 예약 단위 타입이다. 정책 미등록 시 null.
    private FacilityUsageUnitType usageUnitType;

    // 예약 단위 타입 표시명이다. 정책 미등록 시 null.
    private String usageUnitTypeName;

    // 시설 최대 동시 이용 인원이다. 미설정 시 null.
    private Integer maxCount;

    // 예약 단위 표시용 라벨이다. 정책 미등록 시 null. 예: "60분", "하루"
    private String reservationUnitLabel;
}
