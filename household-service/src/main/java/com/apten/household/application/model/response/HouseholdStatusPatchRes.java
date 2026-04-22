package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 상태 변경 응답
@Getter
@Builder
public class HouseholdStatusPatchRes {
    private String householdUid;
    private String status;
    private LocalDateTime changedAt;
}
