package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 관리자 세대별 구독 현황 요약 응답 DTO이다.
@Getter
@Builder
public class AdminHouseholdSubscriptionSummaryRes {

    // 세대 ID이다.
    private Long householdId;

    // 동 정보이다.
    private String buildingNo;

    // 호 정보이다.
    private String unitNo;

    // 구독 중인 시설 수이다.
    private int activeCount;

    // 해지된 시설 수이다.
    private int cancelledCount;
}
