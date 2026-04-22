package com.apten.household.application.model.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 세대 상세 조회 응답
@Getter
@Builder
public class HouseholdGetDetailRes {
    private Long householdId;
    private String householdUid;
    private String building;
    private String unit;
    private String status;
    private HouseholdTypeInfo householdType;
    private HouseholdHeadInfo head;
    private List<HouseholdMemberInfo> members;
    private Integer carCount;
    private BigDecimal visitorUsageHours;
    private List<MonthlyBillInfo> monthlyBills;

    // 세대 유형 요약 정보
    @Getter
    @Builder
    public static class HouseholdTypeInfo {
        private String householdTypeUid;
        private String householdTypeName;
    }

    // 세대주 요약 정보
    @Getter
    @Builder
    public static class HouseholdHeadInfo {
        private String userUid;
        private String name;
        private String phone;
    }

    // 세대원 요약 정보
    @Getter
    @Builder
    public static class HouseholdMemberInfo {
        private String userUid;
        private String name;
        private String role;
        private Boolean isActive;
    }

    // 월 청구 요약 정보
    @Getter
    @Builder
    public static class MonthlyBillInfo {
        private String billUid;
        private String billingYearMonth;
        private BigDecimal totalAmount;
        private String status;
    }
}
