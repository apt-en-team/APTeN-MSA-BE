package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차층 수정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingFloorPatchReq {

    // 층 이름이다.
    private String floorName;

    // 전체 주차 면수이다.
    private Integer totalSlots;

    // 활성 여부이다.
    private Boolean isActive;
}
