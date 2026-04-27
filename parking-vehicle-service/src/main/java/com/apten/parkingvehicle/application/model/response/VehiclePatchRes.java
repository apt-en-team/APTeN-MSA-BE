package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 수정 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePatchRes {

    // 차량 ID이다.
    private Long vehicleId;

    // 차량 모델명이다.
    private String modelName;

    // 차량 종류이다.
    private VehicleType vehicleType;

    // 대표 차량 여부이다.
    private Boolean isPrimary;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
