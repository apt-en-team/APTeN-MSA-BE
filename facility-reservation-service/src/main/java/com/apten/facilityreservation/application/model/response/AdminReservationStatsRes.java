package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 관리자 예약 통계 응답 DTO이다.
@Getter
@Builder
public class AdminReservationStatsRes {

    // 오늘 예약 건수 (시설 예약 기준 reservationDate = 오늘)
    private long todayTotal;

    // 오늘 예약 건수 (CONFIRMED + COMPLETED, 시설 예약 기준)
    private long todayReserved;

    // 전체 GX 대기 신청 건수 (WAITING)
    private long gxWaiting;

    // 오늘 취소 건수 (CANCELLED, 시설 예약 기준)
    private long todayCancelled;

    // 진행 중 예약 건수 (FACILITY CONFIRMED + GX CONFIRMED + GX WAITING)
    private long activeCount;

    // 취소된 예약 건수 (FACILITY CANCELLED + GX CANCELLED)
    private long cancelledCount;

    // 이번 달 누적 예약 건수 (FACILITY + GX, 생성일 기준)
    private long monthlyTotal;
}
