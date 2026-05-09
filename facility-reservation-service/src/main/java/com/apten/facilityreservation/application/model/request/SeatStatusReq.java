package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 상태 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatusReq {

    // 조회 기준일이다.
    private LocalDate targetDate;

    // 조회 시작 시각이다.
    private LocalTime startTime;

    // 조회 종료 시각이다.
    private LocalTime endTime;
}
