package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 비용 산정 실행 응답
@Getter
@Builder
public class HouseholdBillCalcRes {
    private String apartmentComplexUid;
    private String billingYearMonth;
    private Integer affectedHouseholdCount;
    private LocalDateTime calculatedAt;
}
