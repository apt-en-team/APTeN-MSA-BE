package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 차단 시간 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityBlockTimePostReq {

    // 차단일이다.
    private LocalDate blockDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 차단 사유이다.
    private String reason;

    // 활성 여부이다.
    private Boolean isActive;
}
