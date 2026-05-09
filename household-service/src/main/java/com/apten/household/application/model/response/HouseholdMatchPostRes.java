package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 매칭 요청 생성 응답
@Getter
@Builder
public class HouseholdMatchPostRes {

    // 매칭 요청 ID이다.
    private Long matchRequestId;

    // 매칭된 세대 ID이다.
    private Long matchedHouseholdId;

    // 처리 방식이다.
    private String processType;

    // 매칭 상태이다.
    private String matchStatus;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
