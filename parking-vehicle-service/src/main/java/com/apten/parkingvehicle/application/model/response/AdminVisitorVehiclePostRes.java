package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 방문차량 등록 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVisitorVehiclePostRes {
    private Long visitorVehicleId;
    private String visitorVehicleUid;
    private Long householdId;
    private String licensePlate;
    private LocalDate visitDate;
    private String status;
    private LocalDateTime createdAt;
}
