package com.apten.parkingvehicle.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 방문차량 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVisitorVehiclePostReq {
    private Long householdId;
    private String licensePlate;
    private String visitorName;
    private String phone;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String memo;
}
