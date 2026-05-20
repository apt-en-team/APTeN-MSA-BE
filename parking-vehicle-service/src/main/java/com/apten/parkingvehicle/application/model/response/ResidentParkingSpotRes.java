package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 입주민 자리 맵 화면용 자리 단위 응답 DTO이다.
// 자리별 현재 점유 상태는 Redis Hash status에서 가져오며, 없으면 "UNKNOWN"으로 내려준다.
@Getter
@Builder
public class ResidentParkingSpotRes {

    // 주차 센서 ID이다.
    private Long sensorId;

    // 소속 주차 구역 ID이다.
    private Long zoneId;

    // 자리 번호이다.
    private String spotNumber;

    // 센서 식별 코드이다. (FE가 spot-changed SSE 이벤트와 매칭하는 키)
    private String sensorCode;

    // 센서 활성 여부이다. (자리 자체가 살아있냐 여부)
    private Boolean isActive;

    // 현재 점유 상태이다. ("OCCUPIED" | "VACANT" | "UNKNOWN")
    private String status;
}
