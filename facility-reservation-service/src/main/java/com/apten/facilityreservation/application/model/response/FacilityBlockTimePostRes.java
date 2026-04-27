package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 시설 차단 시간 등록 응답 DTO이다.
@Getter
@Builder
public class FacilityBlockTimePostRes {

    // 차단 시간 ID이다.
    private Long facilityBlockTimeId;

    // 시설 ID이다.
    private Long facilityId;

    // 차단일이다.
    private LocalDate blockDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 사유이다.
    private String reason;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
