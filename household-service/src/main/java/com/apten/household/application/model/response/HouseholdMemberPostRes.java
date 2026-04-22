package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대원 등록 응답
@Getter
@Builder
public class HouseholdMemberPostRes {
    private String householdMemberUid;
    private String householdUid;
    private String userUid;
    private String role;
    private LocalDateTime createdAt;
}
