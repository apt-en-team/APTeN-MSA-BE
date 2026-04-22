package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 월별 비용 확정 응답
@Getter
@Builder
public class HouseholdBillConfirmRes {
    private String billUid;
    private String status;
    private LocalDateTime confirmedAt;
}
