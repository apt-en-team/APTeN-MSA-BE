package com.apten.parkingvehicle.infrastructure.kafka.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 비용 산정 결과 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorFeeCalculatedEventPayload {

    // 단지 ID이다.
    private Long complexId;

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;

    // 세대별 방문차량 비용 목록이다.
    private List<Item> items;

    // 발생 시각이다.
    private LocalDateTime occurredAt;

    // 세대별 방문차량 비용 항목이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        // 세대 ID이다.
        private Long householdId;

        // 총 이용 분이다.
        private Integer totalMinutes;

        // 총 이용 시간이다.
        private BigDecimal totalHours;

        // 무료 이용 분이다.
        private Integer freeMinutes;

        // 초과 이용 분이다.
        private Integer extraMinutes;

        // 방문차량 비용이다.
        private BigDecimal visitorFee;
    }
}
