package com.apten.facilityreservation.application.model.response;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityGetRes {
    private Long facilityId;
    private String facilityUid;
    private String facilityTypeUid;
    private String name;
    private String description;
    private String reservationType;
    private Integer maxCount;
    private Integer slotMin;
    private Boolean isActive;
    private LocalTime openTime;
    private LocalTime closeTime;
}
