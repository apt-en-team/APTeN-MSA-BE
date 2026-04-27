package com.apten.household.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 청구 기본 정책 설정 요청 DTO이다.
// 기본 관리비와 납부기한, 연체료율을 한 번에 받을 때 사용한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPolicyPutReq {

    // 세대 청구에 공통으로 들어갈 기본 관리비이다.
    private BigDecimal baseFee;

    // 매월 납부 마감일이다.
    private Integer paymentDueDay;

    // 월 기준 연체료율이다.
    private BigDecimal lateFeeRate;

    // 정책 활성 여부이다.
    private Boolean isActive;
}
