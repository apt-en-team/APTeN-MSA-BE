package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 센서 단건 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSensorPostReq {

    // 소속 주차 구역 ID이다.
    private Long zoneId;

    // 자리 번호이다.
    private String spotNumber;

    // 센서 식별 코드이다.
    private String sensorCode;

    // 자리 부가 설명이다. (NULL 가능)
    private String description;
}
