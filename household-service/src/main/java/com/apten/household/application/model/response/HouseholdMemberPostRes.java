package com.apten.household.application.model.response;

import com.apten.household.domain.enums.HouseholdMemberRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대원 등록 응답
@Getter
@Builder
public class HouseholdMemberPostRes {

    // 세대원 ID이다.
    private Long householdMemberId;

    // 세대 ID이다.
    private Long householdId;

    // 사용자 ID이다.
    private Long userId;

    // 세대원 역할이다.
    private HouseholdMemberRole role;

    // 활성 여부이다.
    private Boolean isActive;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
