package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 월 집계 실행 결과 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorUsageMonthlyAggregateRes {
    private Integer targetYear;
    private Integer targetMonth;
    private Integer aggregatedHouseholdCount;
    private Boolean published;
    private LocalDateTime executedAt;
}
