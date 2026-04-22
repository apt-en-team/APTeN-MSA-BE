package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 수동 승인 처리 응답
@Getter
@Builder
public class HouseholdMatchApproveRes {
    private String matchRequestUid;
    private String userUid;
    private String status;
    private LocalDateTime approvedAt;
}
