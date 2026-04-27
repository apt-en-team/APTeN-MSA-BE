package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 타입 등록 응답 DTO이다.
@Getter
@Builder
public class FacilityTypePostRes {

    // 시설 타입 ID이다.
    private Long facilityTypeId;

    // 시설 타입 코드이다.
    private FacilityTypeCode typeCode;

    // 타입명이다.
    private String typeName;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
