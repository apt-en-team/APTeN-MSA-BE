package com.apten.facilityreservation.application.model.request;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPostReq {
    private String facilityTypeUid;
    private String name;
    private String description;
    private String reservationType;
    private Integer maxCount;
    private Integer slotMin;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isActive;
}
