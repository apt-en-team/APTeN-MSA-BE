package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 가능 시간 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAvailableTimeSearchReq {
    private String facilityUid;
    private LocalDate reservationDate;
}
