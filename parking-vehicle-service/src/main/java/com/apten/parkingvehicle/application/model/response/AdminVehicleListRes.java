package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import com.apten.parkingvehicle.domain.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 차량 목록 항목 응답 DTO이다.
@Getter
@Builder
public class AdminVehicleListRes {

    // 차량 ID이다.
    private Long vehicleId;

    // 차량 번호이다.
    private String licensePlate;

    // 차량 모델명이다.
    private String modelName;

    // 차량 종류이다.
    private VehicleType vehicleType;

    // 차량 상태이다.
    private VehicleStatus status;

    // 사용자 이름이다.
    private String residentName;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
