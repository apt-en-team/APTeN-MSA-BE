package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 비용 산정 응답 DTO이다.
@Getter
@Builder
public class VehicleFeeCalculateRes {

    // 단지 ID이다.
    private Long complexId;

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;

    // 세대별 산정 항목이다.
    private List<Item> items;

    // 발행 여부이다.
    private Boolean published;

    // 실행 시각이다.
    private LocalDateTime executedAt;

    // 세대별 차량 비용 산정 결과 항목이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        // 세대 ID이다.
        private Long householdId;

        // 승인 차량 수이다.
        private Integer approvedVehicleCount;

        // 차량 비용이다.
        private BigDecimal vehicleFee;
    }
}
