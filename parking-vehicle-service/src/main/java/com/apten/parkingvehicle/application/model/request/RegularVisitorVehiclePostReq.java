package com.apten.parkingvehicle.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 고정 방문차량 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularVisitorVehiclePostReq {
    private String licensePlate;
    private String visitorName;
    private String phone;
    private LocalDate startDate;
    private LocalDate endDate;
}
