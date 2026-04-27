package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 좌석 등록 응답 DTO이다.
@Getter
@Builder
public class FacilitySeatPostRes {

    // 좌석 ID이다.
    private Long seatId;

    // 시설 ID이다.
    private Long facilityId;

    // 좌석 번호이다.
    private Integer seatNo;

    // 좌석명이다.
    private String seatName;

    // 활성 여부이다.
    private Boolean isActive;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
