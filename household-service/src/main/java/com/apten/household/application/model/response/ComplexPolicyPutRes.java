package com.apten.household.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 기본 관리비 정책 설정 응답 DTO이다.
@Getter
@Builder
public class ComplexPolicyPutRes {

    // 단지 ID이다.
    private Long complexId;

    // 기본 관리비이다.
    private BigDecimal baseFee;

    // 납부기한일이다.
    private Integer dueDay;

    // 연체료율이다.
    private BigDecimal lateFeeRate;

    // 정책 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
