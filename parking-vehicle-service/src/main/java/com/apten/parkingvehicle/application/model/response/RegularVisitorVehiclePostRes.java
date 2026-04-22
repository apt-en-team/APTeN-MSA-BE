package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 고정 방문차량 등록 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularVisitorVehiclePostRes {
    private Long regularVisitorVehicleId;
    private String regularVisitorVehicleUid;
    private String licensePlate;
    private String visitorName;
    private String phone;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
