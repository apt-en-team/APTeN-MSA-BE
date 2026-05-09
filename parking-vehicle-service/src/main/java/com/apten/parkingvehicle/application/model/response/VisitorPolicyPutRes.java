package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 방문차량 정책 설정 응답 DTO이다.
// 저장된 방문차량 정책 결과를 내려줄 때 사용한다.
@Getter
@Builder
public class VisitorPolicyPutRes {

    // 단지 ID이다.
    private final Long complexId;

    // 무료 허용 시간이다.
    private final Integer freeMinutes;

    // 시간당 요금이다.
    private final BigDecimal hourFee;

    // 월 기준 최대 허용 시간이다.
    private final Integer monthlyLimitHours;

    // 정책 활성 여부이다.
    private final Boolean isActive;

    // 정책 저장이 끝난 시각이다.
    private final LocalDateTime updatedAt;
}
