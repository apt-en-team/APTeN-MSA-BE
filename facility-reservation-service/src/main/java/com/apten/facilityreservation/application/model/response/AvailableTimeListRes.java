package com.apten.facilityreservation.application.model.response;

import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 예약 가능 시간 조회 응답 DTO이다.
@Getter
@Builder
public class AvailableTimeListRes {

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 예약 가능 수량이다.
    private Integer availableCount;

    // 전체 수량이다.
    private Integer totalCount;

    // 예약 가능 여부이다.
    private Boolean isReservable;
}
