package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 등록 신청 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreateReq {

    // 차량 번호이다.
    private String licensePlate;

    // 차량 모델명이다.
    private String modelName;

    // 차량 종류이다.
    private VehicleType vehicleType;

    // 대표 차량 여부이다.
    private Boolean isPrimary;
}
