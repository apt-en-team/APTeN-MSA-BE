package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대원 수정 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberPatchReq {

    // 변경할 세대원 역할이다.
    private HouseholdMemberRole role;

    // 활성 여부이다.
    private Boolean isActive;
}
