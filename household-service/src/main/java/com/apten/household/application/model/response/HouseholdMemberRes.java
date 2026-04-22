package com.apten.household.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 세대원 조회 응답
@Getter
@Builder
public class HouseholdMemberRes {
    private String householdMemberUid;
    private String userUid;
    private String name;
    private String phone;
    private String role;
    private Boolean isActive;
}
