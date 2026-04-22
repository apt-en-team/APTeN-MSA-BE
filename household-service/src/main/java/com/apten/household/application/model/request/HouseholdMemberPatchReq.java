package com.apten.household.application.model.request;

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
    private String role;
    private Boolean isActive;
}
