package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 예약 취소 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxReservationCancelRes {
    private String gxReservationUid;
    private String status;
    private LocalDateTime cancelledAt;
}
