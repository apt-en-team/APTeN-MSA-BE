package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import com.apten.parkingvehicle.domain.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 내 차량 목록 항목 응답 DTO이다.
@Getter
@Builder
public class VehicleListRes {

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

    // 대표 차량 여부이다.
    private Boolean isPrimary;

    // 승인 시각이다.
    private LocalDateTime approvedAt;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
