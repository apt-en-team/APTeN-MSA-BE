package com.apten.facilityreservation.application.model.response;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 가능 시간 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAvailableTimeRes {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer availableCount;
    private Integer totalCount;
    private Boolean isReservable;
}
