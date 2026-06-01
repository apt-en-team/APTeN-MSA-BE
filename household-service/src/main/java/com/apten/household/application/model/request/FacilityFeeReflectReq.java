package com.apten.household.application.model.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 비용 반영 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityFeeReflectReq {

    // 단지 ID이다.
    private Long complexId;

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;
    // 세대별 시설 이용 비용 목록이다. Kafka 비용 산정 이벤트를 직접 반영할 때 사용한다.
    private List<Item> items;

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
