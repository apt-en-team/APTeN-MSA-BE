package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 주차 센서 응답 DTO이다. (단건과 목록 항목 공용)
@Getter
@Builder
public class ParkingSensorRes {

    // 주차 센서 ID이다.
    private Long sensorId;

    // 소속 주차 구역 ID이다.
    private Long zoneId;

    // 소속 주차 구역 이름이다. (화면 표시용)
    private String zoneName;

    // 자리 번호이다.
    private String spotNumber;

    // 센서 식별 코드이다.
    private String sensorCode;

    // 자리 부가 설명이다. (NULL 가능)
    private String description;

    // 활성 여부이다.
    private Boolean isActive;

    // 생성 시각이다.
    private LocalDateTime createdAt;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
