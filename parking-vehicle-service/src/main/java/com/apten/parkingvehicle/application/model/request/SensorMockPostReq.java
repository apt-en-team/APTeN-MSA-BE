package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.SensorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Mock 시연용 센서 초기 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorMockPostReq {

    // 센서 식별 코드이다.
    private String sensorCode;

    // 소속 주차 구역 ID이다.
    private Long zoneId;

    // 소속 단지 ID이다.
    private Long complexId;

    // 초기 점유 상태이다.
    private SensorStatus initialStatus;
}
