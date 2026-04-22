package com.apten.household.application.model.request;

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
    private String status;
    private String reason;
}
