package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

// 입주민 나의 시설 구독 목록 조회 응답 DTO이다.
@Getter
@Builder
public class ResidentFacilitySubscriptionListRes {

    // 구독 ID이다.
    private Long subscriptionId;

    // 시설 ID이다.
    private Long facilityId;

    // 시설 이름이다.
    private String facilityName;

    // 요금 청구 방식이다.
    private FacilityFeeType feeType;

    // 구독 시작일이다.
    private LocalDate subscribedAt;

    // 구독 해지 요청일이다. 활성 구독이면 null이다.
    private LocalDate cancelledAt;

    // 구독 상태이다.
    private FacilitySubscriptionStatus status;

    // 기본 요금이다. (정책이 없으면 null)
    private BigDecimal baseFee;

    // 구독 신청 월정산 기준일이다. null이면 항상 당월 청구한다.
    private Integer subscribeCutoffDay;

    // 구독 해지 월정산 기준일이다. null이면 항상 당월 청구한다.
    private Integer cancelCutoffDay;
}
