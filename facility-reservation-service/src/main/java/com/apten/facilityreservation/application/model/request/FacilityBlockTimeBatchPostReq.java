package com.apten.facilityreservation.application.model.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 반복 차단 시간 배치 등록 요청 DTO이다. daysOfWeek×[validFrom,validUntil] 범위 날짜마다 차단 행을 생성한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityBlockTimeBatchPostReq {

    private List<DayOfWeek> daysOfWeek;

    private LocalDate validFrom;

    private LocalDate validUntil;

    // null이면 종일 차단
    private LocalTime startTime;

    // null이면 종일 차단
    private LocalTime endTime;

    // null이면 시설 전체 차단
    private Long seatId;

    private String reason;
}
