package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 구역 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingZonePostReq {

    // 주차장 단위 이름이다.
    private String areaName;

    // 구역 단위 이름이다. (C타입만 사용, NULL 가능)
    private String zoneName;

    // 전체 주차 면수이다.
    private Integer totalSlots;

    // 활성 여부이다.
    private Boolean isActive;
}