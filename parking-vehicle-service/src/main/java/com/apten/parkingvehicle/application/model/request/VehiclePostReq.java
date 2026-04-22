package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 입주민 차량 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePostReq {
    private String licensePlate;
    private String modelName;
    private String vehicleType;
    private Boolean isPrimary;
}
