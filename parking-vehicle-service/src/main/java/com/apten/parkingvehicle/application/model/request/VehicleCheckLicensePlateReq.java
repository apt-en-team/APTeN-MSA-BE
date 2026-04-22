package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량번호 중복 확인 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCheckLicensePlateReq {
    private String licensePlate;
}
