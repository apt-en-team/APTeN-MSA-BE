package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 예약 강제 취소 응답 DTO이다.
@Getter
@Builder
public class AdminReservationCancelRes {

    // 예약 ID이다.
    private Long reservationId;

    // 예약 상태이다.
    private ReservationStatus status;

    // 취소 사유이다.
    private String reason;

    // 취소 시각이다.
    private LocalDateTime cancelledAt;
}
