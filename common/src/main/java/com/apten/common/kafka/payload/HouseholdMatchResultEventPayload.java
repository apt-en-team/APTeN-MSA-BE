package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 매칭 승인/거절 결과 이벤트 payload
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMatchResultEventPayload {

    // 세대 매칭 요청 ID
    private Long matchRequestId;

    // 상태 변경 대상 사용자 ID
    private Long userId;

    // 단지 ID
    private Long complexId;

    // 매칭 상태 (APPROVED / REJECTED)
    private String matchStatus;

    // 매칭된 세대 ID
    private Long householdId;
}