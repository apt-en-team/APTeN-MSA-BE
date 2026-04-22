package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 수동 승인 대상 목록의 한 행 응답
@Getter
@Builder
public class HouseholdMatchGetRes {
    private String matchRequestUid;
    private String userUid;
    private String applicantName;
    private String building;
    private String unit;
    private String status;
    private LocalDateTime createdAt;
}
