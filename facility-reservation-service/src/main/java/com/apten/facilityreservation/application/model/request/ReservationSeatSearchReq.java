package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 상태 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSeatSearchReq {
    private String facilityUid;
    private LocalDate reservationDate;
    private LocalTime startTime;
}
