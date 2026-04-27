package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 예약 완료 처리 응답 DTO이다.
@Getter
@Builder
public class ReservationCompleteRes {

    // 완료 처리 건수이다.
    private Integer completedCount;

    // 처리 시각이다.
    private LocalDateTime processedAt;
}
