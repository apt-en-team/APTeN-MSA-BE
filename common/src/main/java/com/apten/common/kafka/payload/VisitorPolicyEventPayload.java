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

    // 정책이 속한 원본 단지 식별자
    private Long complexId;

    // 무료 허용 분
    private Integer freeMinutes;

    // 시간당 요금
    private BigDecimal hourFee;

    // 월 기준 최대 허용 시간
    private Integer monthlyLimitHours;

    // 현재 정책 활성 여부
    private Boolean isActive;
}
