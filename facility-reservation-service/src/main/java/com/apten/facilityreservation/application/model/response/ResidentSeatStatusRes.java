package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 좌석 상태 조회 응답 DTO — 다른 입주민 개인정보는 포함하지 않는다.
@Getter
@Builder
public class ResidentSeatStatusRes {

    // 좌석 ID이다.
    private Long seatId;

    // 좌석 번호이다.
    private Integer seatNo;

    // 좌석명이다.
    private String seatName;

    // 좌석 상태이다. BLOCKED / RESERVED / HOLDING / AVAILABLE
    private String status;

    // HOLDING 상태일 때 임시 선점 만료 시각이다.
    private LocalDateTime holdExpiresAt;
}
