package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 승인 대기 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxWaitingRes {
    private String gxReservationUid;
    private String residentName;
    private Integer waitingNumber;
    private LocalDateTime createdAt;
}
