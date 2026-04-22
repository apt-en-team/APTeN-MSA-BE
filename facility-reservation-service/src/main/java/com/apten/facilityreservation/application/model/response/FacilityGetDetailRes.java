package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 상세 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityGetDetailRes {
    private Long facilityId;
    private String facilityUid;
    private String facilityTypeUid;
    private String name;
    private String description;
    private String reservationType;
    private Integer maxCount;
    private Integer slotMin;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
