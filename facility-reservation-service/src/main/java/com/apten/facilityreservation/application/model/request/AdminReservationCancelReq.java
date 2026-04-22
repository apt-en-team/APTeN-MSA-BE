package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 예약 강제 취소 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationCancelReq {
    private String reason;
}
