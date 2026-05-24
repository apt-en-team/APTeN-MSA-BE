package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 시설 차단 시간 단건 수정 응답 DTO이다.
@Getter
@Builder
public class FacilityBlockTimePatchRes {

    // 수정된 차단 시간 ID이다.
    private Long facilityBlockTimeId;

    // 수정된 차단일이다.
    private LocalDate blockDate;

    // 수정된 시작 시각이다. null이면 하루종일이다.
    private LocalTime startTime;

    // 수정된 종료 시각이다. null이면 하루종일이다.
    private LocalTime endTime;

    // 활성 여부이다.
    private Boolean isActive;
}
