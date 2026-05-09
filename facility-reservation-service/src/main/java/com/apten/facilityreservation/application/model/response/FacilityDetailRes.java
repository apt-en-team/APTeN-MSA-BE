package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 관리자 시설 상세 응답 DTO이다.
@Getter
@Builder
public class FacilityDetailRes {

    // 시설 ID이다.
    private Long facilityId;

    // 시설 타입 ID이다.
    private Long typeId;

    // 시설 타입명이다.
    private String typeName;

    // 시설명이다.
    private String name;

    // 시설 설명이다.
    private String description;

    // 예약 방식이다.
    private ReservationType reservationType;

    // 최대 인원이다.
    private Integer maxCount;

    // 운영 시작 시간이다.
    private LocalTime openTime;

    // 운영 종료 시간이다.
    private LocalTime closeTime;

    // 시설 override 예약 단위이다.
    private Integer slotMin;

    // 시설 override 기본 요금이다.
    private BigDecimal baseFee;

    // 활성 여부이다.
    private Boolean isActive;

    // 좌석 목록이다.
    private List<SeatItem> seats;

    // 생성 시각이다.
    private LocalDateTime createdAt;

    // 수정 시각이다.
    private LocalDateTime updatedAt;

    // 좌석 내부 DTO이다.
    @Getter
    @Builder
    public static class SeatItem {

        private Long seatId;
        private Integer seatNo;
        private String seatName;
        private Boolean isActive;
    }
}
