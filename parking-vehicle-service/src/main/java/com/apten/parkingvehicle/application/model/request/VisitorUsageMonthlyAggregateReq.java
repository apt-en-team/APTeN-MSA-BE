package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 월 집계 실행 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorUsageMonthlyAggregateReq {
    private Integer targetYear;
    private Integer targetMonth;
}
