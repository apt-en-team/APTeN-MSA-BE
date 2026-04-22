package com.apten.household.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 기본 관리비 정책 설정 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdBillPolicyPutReq {
    private String apartmentComplexUid;
    private BigDecimal baseFee;
    private Integer defaultSlotMin;
    private Boolean isActive;
}
