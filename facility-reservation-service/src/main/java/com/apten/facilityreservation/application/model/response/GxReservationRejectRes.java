package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// GX 예약 거절 응답 DTO이다.
@Getter
@Builder
public class GxReservationRejectRes {

    // GX 예약 ID이다.
    private Long gxReservationId;

    // 상태이다.
    private GxReservationStatus status;

    // 거절 사유이다.
    private String rejectReason;

    // 처리 시각이다.
    private LocalDateTime updatedAt;
}
