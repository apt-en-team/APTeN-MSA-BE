package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 거절 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRejectReq {
    private String rejectReason;
}
