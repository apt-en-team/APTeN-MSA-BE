package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대원 삭제 응답
@Getter
@Builder
public class HouseholdMemberDeleteRes {
    private String message;
    private LocalDateTime deletedAt;
}
