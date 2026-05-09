package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

// 좌석 상태 조회 응답 DTO이다.
@Getter
@Builder
public class SeatStatusRes {

    // 좌석 ID이다.
    private Long seatId;

    // 좌석 번호이다.
    private Integer seatNo;

    // 좌석명이다.
    private String seatName;

    // 좌석 상태이다.
    private String status;

    // 예약자 이름이다.
    private String residentName;

    // 임시 선점 만료 시각이다.
    private LocalDateTime holdExpiresAt;
}
