package com.apten.facilityreservation.infrastructure.kafka.payload;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 상태 변경 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatusChangedEventPayload {

    // 예약 ID이다.
    private Long reservationId;

    // 단지 ID이다.
    private Long complexId;

    // 사용자 ID이다.
    private Long userId;

    // 시설 ID이다.
    private Long facilityId;

    // 상태 문자열이다.
    private String status;

    // 발생 시각이다.
    private LocalDateTime occurredAt;
}
