package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 센서 수정 요청 DTO이다. (sensorCode와 spotNumber는 수정 불가)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSensorPatchReq {

    // 자리 부가 설명이다. (NULL이면 변경 안 함)
    private String description;

    // 활성 여부이다. (NULL이면 변경 안 함)
    private Boolean isActive;
}
