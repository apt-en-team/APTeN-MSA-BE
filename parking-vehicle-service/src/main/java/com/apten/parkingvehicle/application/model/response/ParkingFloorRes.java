package com.apten.parkingvehicle.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 층 현황 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingFloorRes {
    private Long parkingFloorId;
    private String parkingFloorUid;
    private String floorName;
    private Integer totalSlots;
    private Integer currentParkedCount;
    private Integer remainingSlots;
    private Boolean isActive;
}
