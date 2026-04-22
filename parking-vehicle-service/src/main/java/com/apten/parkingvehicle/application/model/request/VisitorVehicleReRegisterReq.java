package com.apten.parkingvehicle.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 재등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehicleReRegisterReq {
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
