package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdBillStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 월별 비용 확정 응답 DTO이다.
@Getter
@Builder
public class BillConfirmRes {

    // 청구 ID이다.
    private Long billId;

    // 확정 후 청구 상태이다.
    private HouseholdBillStatus status;

    // 확정 시각이다.
    private LocalDateTime confirmedAt;
}
