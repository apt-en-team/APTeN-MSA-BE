package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 상태 변경 응답
@Getter
@Builder
public class HouseholdStatusPatchRes {

    // 세대 ID이다.
    private Long householdId;

    // 변경된 세대 상태이다.
    private HouseholdStatus status;

    // 상태 변경 시각이다.
    private LocalDateTime changedAt;
}
