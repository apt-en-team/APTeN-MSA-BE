package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 방문 예정 차량 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVisitorVehicleRes {
    private Long visitorVehicleId;
    private String visitorVehicleUid;
    private String licensePlate;
    private String visitorName;
    private String phone;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AdminVisitorVehicleHouseholdRes household;
    private String status;
    private LocalDateTime createdAt;
}
