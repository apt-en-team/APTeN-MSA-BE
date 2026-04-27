package com.apten.household.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 청구 기본 정책 설정 응답 DTO이다.
// 저장된 청구 정책 결과를 관리자 화면에 돌려줄 때 사용한다.
@Getter
@Builder
public class BillPolicyPutRes {

    // 정책이 속한 단지 ID이다.
    private Long complexId;

    // 저장된 기본 관리비이다.
    private BigDecimal baseFee;

    // 저장된 납부기한일이다.
    private Integer paymentDueDay;

    // 저장된 연체료율이다.
    private BigDecimal lateFeeRate;

    // 연체료 기준 문자열이다.
    private String lateFeeUnit;

    // 정책 활성 여부이다.
    private Boolean isActive;

    // 정책 저장이 완료된 시각이다.
    private LocalDateTime updatedAt;
}
