package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 방문차량 정책 조회 응답 DTO이다.
@Getter
@Builder
public class VisitorPolicyGetRes {

    // 단지 ID이다.
    private Long complexId;

    // 무료 시간이다.
    private Integer freeMinutes;

    // 시간당 요금이다.
    private BigDecimal hourFee;

    // 월 기준 시간이다.
    private Integer monthlyLimitHours;

    // 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
