package com.apten.apartmentcomplex.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 정책 설정 요청 DTO
// 차량 수 제한과 추가 요금 정책 값을 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePolicyPutReq {
    private Integer maxVehicleCountPerHousehold;
    private Integer freeVehicleCount;
    private BigDecimal extraVehicleFee;
    private Integer visitorFreeMinutes;
    private String memo;
}
