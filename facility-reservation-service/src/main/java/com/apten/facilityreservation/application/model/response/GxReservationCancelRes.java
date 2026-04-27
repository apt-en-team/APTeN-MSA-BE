package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// GX 예약 취소 응답 DTO이다.
@Getter
@Builder
public class GxReservationCancelRes {

    // GX 예약 ID이다.
    private Long gxReservationId;

    // 상태이다.
    private GxReservationStatus status;

    // 취소 시각이다.
    private LocalDateTime cancelledAt;
}
