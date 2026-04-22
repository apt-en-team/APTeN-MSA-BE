package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 현황 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatusRes {
    private Integer totalSlots;
    private Integer currentParkedCount;
    private Integer remainingSlots;
    private BigDecimal occupancyRate;
    private LocalDateTime updatedAt;
}
