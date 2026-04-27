package com.apten.common.kafka.payload;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// vehicle policy cache를 채우는 최소 필드만 담는 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePolicyEventPayload {

    //정책id
    private Long vehiclePolicyId;

    // 정책이 속한 원본 단지 식별자
    private Long apartmentComplexId;

    // 차량 대수 기준
    private Integer carCount;

    // 월 요금
    private BigDecimal monthlyFee;

    // 등록 제한 규칙 사용 여부
    private Boolean isLimitRule;

    // 현재 정책 활성 여부
    private Boolean isActive;
}
