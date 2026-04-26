package com.apten.apartmentcomplex.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 기본 정책 설정 응답 DTO
// 단지 기본 관리비 정책 저장 결과를 내려줄 때 사용한다
@Getter
@Builder
public class ComplexPolicyPutRes {
    // 단지 코드
    private final String complexId;
    // 기본 관리비
    private final BigDecimal baseFee;
    // 납부기한일
    private final Integer paymentDueDay;
    // 연체료율
    private final BigDecimal lateFeeRate;
    // 수정일시
    private final LocalDateTime updatedAt;
}
