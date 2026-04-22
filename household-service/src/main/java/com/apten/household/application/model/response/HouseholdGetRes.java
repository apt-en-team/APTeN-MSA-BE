package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 목록의 한 행 응답
@Getter
@Builder
public class HouseholdGetRes {
    private Long householdId;
    private String householdUid;
    private String building;
    private String unit;
    private String status;
    private String headName;
    private Integer memberCount;
    private Integer carCount;
    private LocalDateTime lastChangedAt;
}
