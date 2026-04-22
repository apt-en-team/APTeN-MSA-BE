package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 수정 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehiclePatchRes {
    private String visitorVehicleUid;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime updatedAt;
}
