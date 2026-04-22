package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 입주민 세대 비용 조회 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdBillSearchReq {
    private String billingYearMonth;
}
