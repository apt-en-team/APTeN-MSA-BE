package com.apten.household.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 전 세대원 이벤트 재발행 응답
@Getter
@Builder
public class HouseholdMemberRepublishRes {

    // 재발행 대상 세대원 수이다.
    private long republishedCount;

    // 처리 결과 메시지이다.
    private String message;
}
