package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationCancelReason;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 예약 상세 응답 DTO이다.
@Getter
@Builder
public class AdminReservationDetailRes {

    // 예약 ID이다.
    private Long reservationId;

    // 사용자 ID이다.
    private Long userId;

    // 입주민 이름이다.
    private String residentName;

    // 동 정보이다.
    private String dong;

    // 호 정보이다.
    private String ho;

    // 동호 통합 표시이다. 예: 101동 202호
    private String unit;

    // 시설 ID이다.
    private Long facilityId;

    // 시설명이다.
    private String facilityName;

    // 예약일이다.
    private LocalDate reservationDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 좌석 번호이다.
    private Integer seatNo;

    // 예약 상태 코드이다. 예: CONFIRMED, CANCELLED, COMPLETED
    private String status;

    // 예약 상태 표시명이다.
    private String statusName;

    // 취소 사유이다.
    private ReservationCancelReason cancelReason;

    // 취소 시각이다.
    private LocalDateTime cancelledAt;

    // 완료 시각이다.
    private LocalDateTime completedAt;

    // 생성 시각이다.
    private LocalDateTime createdAt;

    // 해당 날짜의 확정 예약 인원이다.
    private Long currentCount;

    // 시설 최대 수용 인원이다.
    private Integer maxCount;
}
