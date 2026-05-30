package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 본인 세대 방문차량 월 과금 단건 응답 DTO이다.
@Getter
@Builder
public class VisitorUsageMonthlyGetRes {

    // 단지 ID이다.
    private Long complexId;

    // 세대 ID이다.
    private Long householdId;

    // 사용 연도이다.
    private Integer usageYear;

    // 사용 월이다.
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

    // 발행 시각이다.
    private LocalDateTime publishedAt;
}
