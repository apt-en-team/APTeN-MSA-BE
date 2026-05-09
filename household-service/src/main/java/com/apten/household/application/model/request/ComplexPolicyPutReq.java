package com.apten.household.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 기본 관리비 정책 설정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexPolicyPutReq {

    // 기본 관리비이다.
    private BigDecimal baseFee;

    // 납부기한일이다.
    private Integer dueDay;

    // 연체료율이다.
    private BigDecimal lateFeeRate;

    // 정책 활성 여부이다.
    private Boolean isActive;
}
