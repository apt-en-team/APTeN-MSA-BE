package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 수정 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePatchRes {
    private String vehicleUid;
    private String modelName;
    private String vehicleType;
    private Boolean isPrimary;
    private LocalDateTime updatedAt;
}
