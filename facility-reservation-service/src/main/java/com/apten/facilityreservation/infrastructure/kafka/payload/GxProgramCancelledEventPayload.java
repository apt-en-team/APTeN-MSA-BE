package com.apten.facilityreservation.infrastructure.kafka.payload;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 취소 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgramCancelledEventPayload {

    // 프로그램 ID이다.
    private Long programId;

    // 단지 ID이다.
    private Long complexId;

    // 함께 취소된 예약 ID 목록이다.
    private List<Long> cancelledReservationIds;

    // 취소 사유이다.
    private String reason;

    // 발생 시각이다.
    private LocalDateTime occurredAt;
}
