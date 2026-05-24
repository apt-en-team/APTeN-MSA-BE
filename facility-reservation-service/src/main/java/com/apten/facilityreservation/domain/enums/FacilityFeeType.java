package com.apten.facilityreservation.domain.enums;

// 시설 이용 요금 청구 방식이다.
public enum FacilityFeeType {
    FLAT,       // 세대당 baseFee 월 1회 고정; 초과 인원 요금 옵션(includedPersonCount/extraPersonFee)
    PER_PERSON, // 이용 인원 수 × baseFee 월 청구
    PER_USE     // 예약 완료 건수 × baseFee 건당 청구
}
