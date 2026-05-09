package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import lombok.Builder;
import lombok.Getter;

// 시설 타입 목록 응답 DTO이다.
@Getter
@Builder
public class FacilityTypeListRes {

    // 시설 타입 ID이다.
    private Long facilityTypeId;

    // 시설 타입 코드이다.
    private FacilityTypeCode typeCode;

    // 타입명이다.
    private String typeName;

    // 설명이다.
    private String description;

    // 활성 여부이다.
    private Boolean isActive;
}
