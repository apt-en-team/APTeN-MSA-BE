package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 예약 취소 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationCancelRes {
    private String reservationUid;
    private String status;
    private LocalDateTime cancelledAt;
    private String reason;
}
