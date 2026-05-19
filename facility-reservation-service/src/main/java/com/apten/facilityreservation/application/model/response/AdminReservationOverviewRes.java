package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ReservationKind;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 예약 통합 개요 응답 DTO이다.
@Getter
@Builder
public class AdminReservationOverviewRes {

    // 예약 ID이다.
    private Long reservationId;

    // 예약 종류이다. FACILITY / GX
    private ReservationKind reservationKind;

    // 시설 ID이다.
    private Long facilityId;

    // 시설명이다.
    private String facilityName;

    // GX 프로그램 ID이다. GX 예약 전용이다.
    private Long programId;

    // GX 프로그램명이다. GX 예약 전용이다.
    private String programName;

    // 사용자 ID이다.
    private Long userId;

    // 세대 ID이다.
    private Long householdId;

    // 입주민 이름이다.
    private String residentName;

    // 동 정보이다.
    private String dong;

    // 호 정보이다.
    private String ho;

    // 예약 상태 코드이다. 예: CONFIRMED, WAITING
    private String status;

    // 예약 상태 표시명이다. 예: 예약완료, 대기중
    private String statusName;

    // 예약일이다. 일반 예약 전용이다.
    private LocalDate reservationDate;

    // 신청 시각이다.
    private LocalDateTime createdAt;
}
