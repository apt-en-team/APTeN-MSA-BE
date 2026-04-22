package com.apten.parkingvehicle.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량번호 중복 확인 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCheckLicensePlateRes {
    private Boolean isDuplicate;
}
