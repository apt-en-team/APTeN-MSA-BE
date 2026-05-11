package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdBillStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 월별 비용 확정 취소 응답 DTO이다.
@Getter
@Builder
public class BillUnconfirmRes {

    // 청구 ID이다.
    private Long billId;

    // 취소 후 청구 상태이다.
    private HouseholdBillStatus status;

    // 취소 시각이다.
    private LocalDateTime updatedAt;
}
