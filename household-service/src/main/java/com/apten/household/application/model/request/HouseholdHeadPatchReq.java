package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대주 변경 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdHeadPatchReq {
    private String newHeadUserUid;
}
