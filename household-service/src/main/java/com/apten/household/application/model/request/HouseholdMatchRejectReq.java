package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 매칭 거절 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMatchRejectReq {
    private String reason;
}
