package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대주 변경 응답
@Getter
@Builder
public class HouseholdHeadPatchRes {
    private String householdUid;
    private String previousHeadUserUid;
    private String newHeadUserUid;
    private LocalDateTime updatedAt;
}
