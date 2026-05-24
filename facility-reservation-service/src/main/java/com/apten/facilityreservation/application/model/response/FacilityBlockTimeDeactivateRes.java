package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 차단 시간 단건 비활성화 응답 DTO이다.
@Getter
@Builder
public class FacilityBlockTimeDeactivateRes {

    // 비활성화된 차단 시간 ID이다.
    private Long facilityBlockTimeId;

    // 처리 시각이다.
    private LocalDateTime processedAt;
}
