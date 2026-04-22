package com.apten.facilityreservation.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 유형 목록 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityTypeRes {
    private Long facilityTypeId;
    private String facilityTypeUid;
    private String name;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;
}
