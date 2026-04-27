package com.apten.household.infrastructure.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 매칭 승인 또는 거절 결과 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMatchResultEventPayload {

    // 세대 매칭 요청 ID이다.
    private Long matchRequestId;

    // 사용자 ID이다.
    private Long userId;

    // 단지 ID이다.
    private Long complexId;

    // 매칭 상태 문자열이다.
    private String matchStatus;

    // 매칭된 세대 ID이다.
    private Long householdId;
}
