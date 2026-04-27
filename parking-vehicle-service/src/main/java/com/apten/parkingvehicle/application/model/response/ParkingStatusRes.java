package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 현황 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatusRes {

    // 전체 주차 면수이다.
    private Integer totalSlots;

    // 현재 주차 대수이다.
    private Integer currentParkedCount;

    // 잔여 면수이다.
    private Integer remainingSlots;

    // 점유율이다.
    private BigDecimal occupancyRate;

    // 갱신 시각이다.
    private LocalDateTime updatedAt;
}
