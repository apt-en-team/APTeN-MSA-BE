package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationCancelReason;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 예약 상세 응답 DTO이다.
@Getter
@Builder
public class AdminReservationDetailRes {

    // 예약 ID이다.
    private Long reservationId;

    // 단지 ID이다.
    private Long complexId;

    // 사용자 ID이다.
    private Long userId;

    // 시설 ID이다.
    private Long facilityId;

    // 좌석 ID이다.
    private Long seatId;

    // 예약일이다.
    private LocalDate reservationDate;

    // 시작 시각이다.
    private LocalTime startTime;

    // 종료 시각이다.
    private LocalTime endTime;

    // 예약 상태이다.
    private ReservationStatus status;

    // 취소 사유이다.
    private ReservationCancelReason cancelReason;
}
