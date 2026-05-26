package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 정기 휴무 규칙 비활성화 응답 DTO이다.
@Getter
@Builder
public class FacilityClosureRuleDeactivateRes {

    // 비활성화된 규칙 ID이다.
    private Long closureRuleId;

    // 처리 시각이다.
    private LocalDateTime processedAt;
}
