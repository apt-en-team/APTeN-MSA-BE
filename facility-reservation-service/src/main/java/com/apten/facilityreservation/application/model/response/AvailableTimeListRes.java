package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 가능 시간 조회 응답 DTO이다.
@Getter
@Builder
public class AvailableTimeListRes {

    // 시설 ID이다.
    private Long facilityId;

    // 예약일이다.
    private LocalDate reservationDate;

    // 조회된 시간대 목록이다.
    private List<AvailableSlot> slots;

    // 시간대 정보를 표현하는 내부 DTO이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSlot {

        // 시작 시각이다.
        private LocalTime startTime;

        // 종료 시각이다.
        private LocalTime endTime;

        // 남은 수량이다.
        private Integer availableCount;

        // 예약 가능 여부이다.
        private Boolean available;
    }
}
