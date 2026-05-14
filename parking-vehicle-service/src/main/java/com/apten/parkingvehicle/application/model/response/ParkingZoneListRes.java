package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 주차 구역 목록 항목 응답 DTO이다.
@Getter
@Builder
public class ParkingZoneListRes {

    // 주차 구역 ID이다.
    private Long zoneId;

    // 주차장 단위 이름이다.
    private String areaName;

    // 구역 단위 이름이다. (NULL 가능)
    private String zoneName;

    // 전체 주차 면수이다.
    private Integer totalSlots;

    // 현재 주차 대수이다.
    private Integer currentParkedCount;

    // 잔여 면수이다.
    private Integer remainingSlots;

    // 활성 여부이다.
    private Boolean isActive;
}