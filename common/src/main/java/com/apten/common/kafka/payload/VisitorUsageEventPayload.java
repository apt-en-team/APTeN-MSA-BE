package com.apten.common.kafka.payload;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// household-service의 visitor_usage_snapshot 동기화를 위한 공통 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorUsageEventPayload {

    // 세대 식별자이다.
    private Long householdId;

    // 단지 식별자이다.
    private Long complexId;

    // 집계 연도이다.
    private Integer usageYear;

    // 집계 월이다.
    private Integer usageMonth;

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
