package com.apten.parkingvehicle.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 방문차량 목록의 세대 정보 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVisitorVehicleHouseholdRes {
    private String dong;
    private String ho;
}
