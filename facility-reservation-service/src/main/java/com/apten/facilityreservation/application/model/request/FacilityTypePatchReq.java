package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 타입 수정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityTypePatchReq {

    // 타입명이다.
    private String typeName;

    // 설명이다.
    private String description;

    // 활성 여부이다.
    private Boolean isActive;
}
