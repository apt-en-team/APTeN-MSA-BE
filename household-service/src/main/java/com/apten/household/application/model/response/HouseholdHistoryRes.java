package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 상태 이력 응답
@Getter
@Builder
public class HouseholdHistoryRes {
    private Long historyId;
    private String fromStatus;
    private String toStatus;
    private String reason;
    private LocalDateTime changedAt;
}
