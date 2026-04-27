package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 타입 수정 응답 DTO이다.
@Getter
@Builder
public class FacilityTypePatchRes {

    // 시설 타입 ID이다.
    private Long facilityTypeId;

    // 타입명이다.
    private String typeName;

    // 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
