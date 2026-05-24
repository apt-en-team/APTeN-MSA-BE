package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 차단 시간 단건 수정 요청 DTO이다. blockDate·시작·종료 시각만 변경한다.
@Getter
@NoArgsConstructor
public class FacilityBlockTimePatchReq {

    // 수정할 차단일이다.
    private LocalDate blockDate;

    // 수정할 시작 시각이다. null이면 하루종일로 처리한다.
    private LocalTime startTime;

    // 수정할 종료 시각이다. null이면 하루종일로 처리한다.
    private LocalTime endTime;
}
