package com.apten.facilityreservation.application.model.request;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPatchReq {
    private String name;
    private String description;
    private Integer maxCount;
    private Integer slotMin;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isActive;
}
