package com.apten.parkingvehicle.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 구역별 현재 점유 상태 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingZoneStatusRes {

    // 주차 구역 ID이다.
    private Long zoneId;

    // 주차장 단위 이름이다.
    private String areaName;

    // 구역 단위 이름이다. (NULL 가능)
    private String zoneName;

    // 구역 전체 주차 면수이다.
    private Integer totalSlots;

    // 구역 현재 주차 대수이다.
    private Integer currentParkedCount;

    // 구역 잔여 면수이다.
    private Integer remainingSlots;
}
