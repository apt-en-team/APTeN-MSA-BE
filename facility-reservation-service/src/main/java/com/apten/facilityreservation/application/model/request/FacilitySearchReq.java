package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 목록 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySearchReq {
    private String facilityTypeUid;
    private Boolean isActive;
}
