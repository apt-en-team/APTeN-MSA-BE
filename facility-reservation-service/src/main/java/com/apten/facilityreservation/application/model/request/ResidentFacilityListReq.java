package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 입주민 시설 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentFacilityListReq {

    // 단지 ID 필터이다.
    private Long complexId;

    // 시설 타입 필터이다.
    private FacilityTypeCode facilityTypeCode;
}
