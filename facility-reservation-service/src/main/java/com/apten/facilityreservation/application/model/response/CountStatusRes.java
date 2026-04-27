package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 정원형 이용 현황 조회 응답 DTO이다.
@Getter
@Builder
public class CountStatusRes {

    // 시설 ID이다.
    private Long facilityId;

    // 예약일이다.
    private LocalDate reservationDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 최대 정원이다.
    private Integer maxCount;

    // 예약 인원이다.
    private Integer reservedCount;

    // 남은 정원이다.
    private Integer availableCount;
}
