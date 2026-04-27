package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 좌석 임시 선점 자동 해제 응답 DTO이다.
@Getter
@Builder
public class TempHoldExpireRes {

    // 만료 처리 건수이다.
    private Integer expiredCount;

    // 처리 시각이다.
    private LocalDateTime processedAt;
}
