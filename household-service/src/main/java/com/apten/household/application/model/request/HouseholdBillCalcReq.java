package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 비용 산정 실행 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdBillCalcReq {
    private String apartmentComplexUid;
    private String billingYearMonth;
}
