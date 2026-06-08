package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 관리자 세대별 구독 상세 응답 DTO이다.
@Getter
@Builder
public class AdminHouseholdSubscriptionDetailRes {

    // 세대 ID이다.
    private Long householdId;

    // 동 정보이다.
    private String buildingNo;

    // 호 정보이다.
    private String unitNo;

    // 세대원 목록이다.
    private List<MemberInfo> members;

    // 구독 시설 목록이다.
    private List<SubscriptionInfo> subscriptions;

    // 세대원 정보이다.
    @Getter
    @Builder
    public static class MemberInfo {

        // 사용자 이름이다.
        private String name;

        // 세대 내 역할이다.
        private String memberRole;

        // 대표 세대원 여부이다.
        private boolean isPrimary;
    }

    // 구독 시설 정보이다.
    @Getter
    @Builder
    public static class SubscriptionInfo {

        // 구독 ID이다.
        private Long subscriptionId;

        // 구독 신청자 userId이다.
        private Long userId;

        // 구독 신청자 이름이다.
        private String subscriberName;

        // 시설 ID이다.
        private Long facilityId;

        // 시설명이다.
        private String facilityName;

        // 요금 방식이다.
        private FacilityFeeType feeType;

        // 기본 요금이다.
        private BigDecimal baseFee;

        // 구독 상태이다.
        private FacilitySubscriptionStatus status;

        // 구독 시작일이다.
        private LocalDate subscribedAt;

        // 해지일이다.
        private LocalDate cancelledAt;
    }
}
