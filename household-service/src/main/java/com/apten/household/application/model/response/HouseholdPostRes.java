package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 등록 응답
@Getter
@Builder
public class HouseholdPostRes {
    private Long householdId;
    private String householdUid;
    private String apartmentComplexUid;
    private String building;
    private String unit;
    private String status;
    private LocalDateTime createdAt;
}
