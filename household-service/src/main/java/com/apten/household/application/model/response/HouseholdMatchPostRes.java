package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 매칭 요청 생성 응답
@Getter
@Builder
public class HouseholdMatchPostRes {
    private String matchRequestUid;
    private String userUid;
    private String householdUid;
    private String matchResult;
    private LocalDateTime createdAt;
}
