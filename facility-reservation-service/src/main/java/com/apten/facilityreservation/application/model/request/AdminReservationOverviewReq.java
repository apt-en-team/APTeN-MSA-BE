package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import com.apten.facilityreservation.domain.enums.ReservationKind;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 예약 통합 개요 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationOverviewReq {

    // 예약 종류 필터이다. null이면 전체 조회한다.
    private ReservationKind reservationKind;

    // 시설 ID 필터이다.
    private Long facilityId;

    // 일반 예약 상태 필터이다. reservationKind=FACILITY일 때 적용한다.
    private ReservationStatus facilityStatus;

    // GX 예약 상태 필터이다. reservationKind=GX일 때 적용한다.
    private GxReservationStatus gxStatus;

    // 예약일 필터이다. 일반 예약 전용이다.
    private LocalDate reservationDate;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
