package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대원 삭제 응답
@Getter
@Builder
public class HouseholdMemberDeleteRes {

    // 세대원 ID이다.
    private Long householdMemberId;

    // 처리 결과 메시지이다.
    private String message;

    // 삭제 처리 시각이다.
    private LocalDateTime deletedAt;
}
