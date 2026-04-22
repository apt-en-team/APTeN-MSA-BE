package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 상태 변경 이벤트 발행 결과 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatusChangedEventRes {
    private Boolean published;
    private LocalDateTime publishedAt;
}
