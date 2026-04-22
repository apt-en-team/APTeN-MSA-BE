package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 비용 반영 기준 요약 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehiclePolicySummaryRes {
    private Integer freeMinutes;
    private BigDecimal hourFee;
    private Integer monthlyLimitHours;
    private Boolean isActive;
    private LocalDateTime updatedAt;
}
