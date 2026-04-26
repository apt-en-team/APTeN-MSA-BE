package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// facility policy cache를 채우는 최소 필드만 담는 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicyEventPayload {

    //시설 정책 id
    private Long facilityPolicyId;

    // 정책이 속한 원본 단지 식별자
    private Long apartmentComplexId;

    // 시설 유형 코드
    private String facilityTypeCode;

    // 기본 요금
    private BigDecimal baseFee;

    // 예약 단위 분
    private Integer slotMin;

    // 취소 마감 시간
    private Integer cancelDeadlineHours;

    // GX대기 허용 여부
    private Boolean gxWaitingEnable;

    // 현재 정책 활성 여부
    private Boolean isActive;
}
