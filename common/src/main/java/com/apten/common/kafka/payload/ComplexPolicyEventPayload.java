package com.apten.common.kafka.payload;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// complex policy cache를 채우는 최소 필드만 담는 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexPolicyEventPayload {

    // 정책 id
    private Long complexPolicyId;

    // 정책이 속한 원본 단지 식별자
    private Long apartmentComplexId;

    // 기본 관리비 금액
    private BigDecimal baseFee;

    // 매월 납부 마감 일자
    private Integer paymentDueDay;

    // 연체료 비율
    private BigDecimal lateFeeRate;

    // 연체료 계산 기준 단위
    private String lateFeeUnit;

    // 현재 정책 활성 여부
    private Boolean isActive;
}
