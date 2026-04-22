package com.apten.household.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 기본 관리비 정책 설정 응답
@Getter
@Builder
public class HouseholdBillPolicyPutRes {
    private String apartmentComplexUid;
    private BigDecimal baseFee;
    private Integer defaultSlotMin;
    private LocalDateTime updatedAt;
}
