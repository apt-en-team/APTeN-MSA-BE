package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대주 변경 응답
@Getter
@Builder
public class HouseholdHeadPatchRes {

    // 세대 ID이다.
    private Long householdId;

    // 새 세대주 사용자 ID이다.
    private Long headUserId;

    // 변경 시각이다.
    private LocalDateTime updatedAt;
}
