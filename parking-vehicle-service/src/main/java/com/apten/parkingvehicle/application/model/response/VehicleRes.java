package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내 차량 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRes {
    private Long vehicleId;
    private String vehicleUid;
    private String licensePlate;
    private String modelName;
    private String vehicleType;
    private String status;
    private Boolean isPrimary;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
