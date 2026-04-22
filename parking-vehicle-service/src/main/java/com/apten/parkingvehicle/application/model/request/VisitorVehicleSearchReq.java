package com.apten.parkingvehicle.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내 방문차량 목록 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehicleSearchReq {
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer page;
    private Integer size;
}
