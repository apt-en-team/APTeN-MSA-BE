package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 상태 변경 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdStatusPatchReq {

    // 변경할 세대 상태이다.
    private HouseholdStatus status;

    // 상태 변경 사유이다.
    private String reason;
}
