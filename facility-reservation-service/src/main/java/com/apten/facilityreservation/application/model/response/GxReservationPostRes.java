package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 예약 신청 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxReservationPostRes {
    private Long gxReservationId;
    private String gxReservationUid;
    private String gxProgramUid;
    private Integer waitingNumber;
    private String status;
    private LocalDateTime createdAt;
}
