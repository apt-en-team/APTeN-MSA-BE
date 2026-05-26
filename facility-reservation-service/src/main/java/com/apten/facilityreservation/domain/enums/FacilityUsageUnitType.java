package com.apten.facilityreservation.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 시설 정책 예약 단위 타입이다.
@Getter
@RequiredArgsConstructor
public enum FacilityUsageUnitType {
    MINUTE("분"),
    DAY("일");

    private final String label;
}
