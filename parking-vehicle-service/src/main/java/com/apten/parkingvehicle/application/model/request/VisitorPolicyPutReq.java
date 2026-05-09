package com.apten.parkingvehicle.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 정책 설정 요청 DTO이다.
// 무료 시간과 시간당 요금 정책을 받을 때 사용한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPolicyPutReq {

    // 무료 허용 시간이다.
    private Integer freeMinutes;

    // 시간당 요금이다.
    private BigDecimal hourFee;

    // 월 기준 최대 허용 시간이다.
    private Integer monthlyLimitHours;

    // 정책 활성 여부이다.
    private Boolean isActive;
}
