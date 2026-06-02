package com.apten.common.kafka.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 이용 비용 산정 이벤트 payload — facility-reservation-service → household-service
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityFeeCalculatedEventPayload {

    private Long complexId;
    private Integer usageYear;
    private Integer usageMonth;
    private List<Item> items;
    private LocalDateTime occurredAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long householdId;
        private BigDecimal facilityFee;
    }
}
