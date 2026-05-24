package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

// 정원형 이용 현황 조회 요청 DTO이다.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountStatusReq {

    // 조회 기준일이다.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetDate;

    // 시작 시각이다.
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;

    // 종료 시각이다.
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;
}
