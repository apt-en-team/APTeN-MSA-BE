package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대원 수정 응답
@Getter
@Builder
public class HouseholdMemberPatchRes {
    private String householdMemberUid;
    private String role;
    private Boolean isActive;
    private LocalDateTime updatedAt;
}
