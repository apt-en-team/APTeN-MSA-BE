package com.apten.parkingvehicle.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 정책 설정 요청 DTO이다.
// 시간당 요금과 월 기본 제공 시간 정책을 받을 때 사용한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPolicyPutReq {

    // 월 기본 제공 시간 초과 시 시간당 요금이다.
    private BigDecimal hourFee;

    // 월 기본 제공 시간이다.
    private Integer monthlyLimitHours;

    // 정책 활성 여부이다.
    private Boolean isActive;
}
