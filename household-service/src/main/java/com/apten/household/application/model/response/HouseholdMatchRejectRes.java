package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 수동 거절 처리 응답
@Getter
@Builder
public class HouseholdMatchRejectRes {

    // 매칭 요청 ID이다.
    private Long matchRequestId;

    // 처리 상태이다.
    private String matchStatus;

    // 처리 시각이다.
    private LocalDateTime processedAt;

    // 거절 사유이다.
    private String reason;
}
