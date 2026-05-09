package com.apten.household.application.model.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 비용 반영 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorFeeReflectReq {

    // 단지 ID이다.
    private Long complexId;

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;

    // 세대별 방문차량 이용시간과 비용 반영 목록이다.
    private List<Item> items;

    // 세대별 방문차량 비용 반영 항목이다.
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
