package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.ParkingTargetDateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 방문 예정 차량 목록 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVisitorVehicleSearchReq {
    private ParkingTargetDateType targetDateType;
    private String keyword;
    private Integer page;
    private Integer size;
}
