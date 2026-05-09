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

    // 새로 세대주로 지정할 사용자 ID이다.
    private Long userId;
}
