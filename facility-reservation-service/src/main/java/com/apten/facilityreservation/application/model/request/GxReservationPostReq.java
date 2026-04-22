package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 예약 신청 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxReservationPostReq {
    private String gxProgramUid;
}
