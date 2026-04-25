package com.apten.common.kafka.payload;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// visitor policy cache를 채우는 최소 필드만 담는 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPolicyEventPayload {

    // 정책이 속한 원본 단지 식별자이다.
    private Long apartmentComplexId;

    // 무료 허용 분이다.
    private Integer freeMinutes;

    // 초과 요금 단가이다.
    private BigDecimal extraFeePerUnit;

    // 초과 요금 계산 단위 분이다.
    private Integer extraFeeUnitMinutes;

    // 일 최대 요금이다.
    private BigDecimal dailyMaxFee;

    // 현재 정책 활성 여부이다.
    private Boolean isActive;
}
