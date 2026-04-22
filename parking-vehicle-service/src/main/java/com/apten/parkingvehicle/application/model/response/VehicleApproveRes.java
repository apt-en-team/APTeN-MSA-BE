package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 승인 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleApproveRes {
    private String vehicleUid;
    private String status;
    private LocalDateTime approvedAt;
}
