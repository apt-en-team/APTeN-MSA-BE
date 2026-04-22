package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 등록 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePostRes {
    private Long vehicleId;
    private String vehicleUid;
    private String licensePlate;
    private String modelName;
    private String vehicleType;
    private String status;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
