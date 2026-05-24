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

    // 예약 ID이다. RESERVED 상태일 때만 값이 있다. TSID 정밀도 보호를 위해 문자열로 내려간다.
    private String reservationId;

    // 좌석 번호이다.
    private Integer seatNo;

    // 좌석명이다.
    private String seatName;

    // 좌석 상태이다.
    private String status;

    // 예약자 이름이다.
    private String residentName;

    // 동 정보이다.
    private String dong;

    // 호 정보이다.
    private String ho;

    // 동호 통합 표시이다. 예: 101동 202호
    private String unit;

    // 임시 선점 만료 시각이다.
    private LocalDateTime holdExpiresAt;
}
