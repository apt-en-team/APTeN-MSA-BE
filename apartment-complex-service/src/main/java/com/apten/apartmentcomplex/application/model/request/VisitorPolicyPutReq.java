package com.apten.apartmentcomplex.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 정책 설정 요청 DTO
// 무료 시간과 초과 요금 정책 값을 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPolicyPutReq {

    // 무료 시간
    private Integer freeMinutes;

    // 시간당 요금
    private BigDecimal hourFee;

    // 월 기준 시간
    private Integer monthlyLimitHours;
}