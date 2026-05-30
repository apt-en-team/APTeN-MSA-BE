package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 본인 세대 차량 월 과금 단건 응답 DTO이다.
@Getter
@Builder
public class VehicleFeeMonthlyGetRes {

    // 단지 ID이다.
    private Long complexId;

    // 세대 ID이다.
    private Long householdId;

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;

    // 승인 차량 수이다.
    private Integer approvedVehicleCount;

    // 차량 비용이다.
    private BigDecimal vehicleFee;

    // 발행 시각이다.
    private LocalDateTime publishedAt;
}
