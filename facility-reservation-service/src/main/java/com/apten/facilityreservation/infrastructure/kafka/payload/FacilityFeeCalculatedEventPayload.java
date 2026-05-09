package com.apten.facilityreservation.infrastructure.kafka.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 이용 비용 산정 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityFeeCalculatedEventPayload {

    // 단지 ID이다.
    private Long complexId;

    // 이용 연도이다.
    private Integer usageYear;

    // 이용 월이다.
    private Integer usageMonth;

    // 세대별 비용 목록이다.
    private List<Item> items;

    // 발생 시각이다.
    private LocalDateTime occurredAt;

    // 세대별 비용 내부 DTO이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        // 세대 ID이다.
        private Long householdId;

        // 시설 이용 비용이다.
        private BigDecimal facilityFee;
    }
}
