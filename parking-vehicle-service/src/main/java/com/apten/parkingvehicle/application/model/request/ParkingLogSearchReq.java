package com.apten.parkingvehicle.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 로그 목록 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLogSearchReq {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String licensePlate;
    private String entryType;
    private Integer page;
    private Integer size;
}
