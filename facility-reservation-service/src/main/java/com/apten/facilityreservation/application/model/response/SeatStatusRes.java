package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 상태 조회 응답 DTO이다.
@Getter
@Builder
public class SeatStatusRes {

    // 시설 ID이다.
    private Long facilityId;

    // 예약일이다.
    private LocalDate reservationDate;

    // 좌석 상태 목록이다.
    private List<Item> seats;

    // 좌석 상태 내부 DTO이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        // 좌석 ID이다.
        private Long seatId;

        // 좌석 번호이다.
        private Integer seatNo;

        // 좌석명이다.
        private String seatName;

        // 시작 시각이다.
        private LocalTime startTime;

        // 예약 가능 여부이다.
        private Boolean available;
    }
}
