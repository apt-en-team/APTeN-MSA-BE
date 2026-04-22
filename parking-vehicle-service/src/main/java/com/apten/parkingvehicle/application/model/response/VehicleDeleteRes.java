package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 삭제 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDeleteRes {
    private String message;
    private LocalDateTime deletedAt;
}
