package com.apten.household.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 세대 비용 조회 응답
@Getter
@Builder
public class HouseholdBillRes {
    private String billUid;
    private String billingYearMonth;
    private BigDecimal baseFee;
    private BigDecimal vehicleFee;
    private BigDecimal facilityFee;
    private BigDecimal visitorFee;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime confirmedAt;
}
