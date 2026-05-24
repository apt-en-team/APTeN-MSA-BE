package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

// 관리자 시설 구독 목록 조회 응답 DTO이다.
@Getter
@Builder
public class AdminFacilitySubscriptionListRes {

    // 구독 ID이다.
    private Long subscriptionId;

    // 소속 단지 ID이다.
    private Long complexId;

    // 세대 ID이다.
    private Long householdId;

    // 시설 ID이다.
    private Long facilityId;

    // 구독 시작일이다.
    private LocalDate subscribedAt;

    // 구독 해지 요청일이다. 활성 구독이면 null이다.
    private LocalDate cancelledAt;

    // 구독 상태이다.
    private FacilitySubscriptionStatus status;
}
