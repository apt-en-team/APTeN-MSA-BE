package com.apten.apartmentcomplex.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 기본 정책 설정 요청 DTO
// 기본 관리비와 예약 단위 정책 값을 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexPolicyPutReq {
    // 기본 관리비
    private BigDecimal baseFee;
    // 납부기한일 (매월 몇일)
    private Integer paymentDueDay;
    // 연체료율 (월 기준 %)
    private BigDecimal lateFeeRate;
}
