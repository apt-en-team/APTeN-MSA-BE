package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 상태 이력 응답
@Getter
@Builder
public class HouseholdHistoryRes {

    // 이력 ID이다.
    private Long historyId;

    // 이전 상태이다.
    private String fromStatus;

    // 변경 후 상태이다.
    private String toStatus;

    // 변경 사유이다.
    private String reason;

    // 상태 변경 시각이다.
    private LocalDateTime changedAt;
}
