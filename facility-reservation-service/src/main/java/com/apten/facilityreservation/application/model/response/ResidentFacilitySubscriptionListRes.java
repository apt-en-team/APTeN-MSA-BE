package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
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
}
