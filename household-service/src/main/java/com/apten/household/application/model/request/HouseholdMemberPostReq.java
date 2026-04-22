package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대원 등록 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberPostReq {
    private String userUid;
    private String role;
}
