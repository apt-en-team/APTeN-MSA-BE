package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 일반 예약 생성 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPostReq {
    private String facilityUid;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer seatNo;
    private Integer quantity;
    private String holdToken;
}
