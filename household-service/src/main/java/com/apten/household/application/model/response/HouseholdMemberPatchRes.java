package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdMemberRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대원 수정 응답
@Getter
@Builder
public class HouseholdMemberPatchRes {

    // 세대원 ID이다.
    private Long householdMemberId;

    // 변경된 세대원 역할이다.
    private HouseholdMemberRole role;

    // 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
