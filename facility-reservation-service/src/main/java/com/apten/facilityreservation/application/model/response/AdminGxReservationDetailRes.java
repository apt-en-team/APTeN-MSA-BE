package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxReservationCancelReason;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 GX 예약 단건 상세 응답 DTO이다.
@Getter
@Builder
public class AdminGxReservationDetailRes {

    // GX 예약 ID이다.
    private Long gxReservationId;

    // GX 프로그램 ID이다.
    private Long programId;

    // GX 프로그램명이다.
    private String programName;

    // 시설 ID이다.
    private Long facilityId;

    // 시설명이다.
    private String facilityName;

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

    // 동호 통합 표시이다. 예: 101동 202호
    private String unit;

    // 예약 상태이다.
    private GxReservationStatus status;

    // 예약 상태 표시명이다.
    private String statusName;

    // 대기 순번이다.
    private Integer waitNo;

    // 프로그램 시작일이다.
    private LocalDate startDate;

    // 프로그램 종료일이다.
    private LocalDate endDate;

    // 운영 시작 시각이다.
    private LocalTime startTime;

    // 운영 종료 시각이다.
    private LocalTime endTime;

    // 기본 요금이다.
    private BigDecimal baseFee;

    // 승인 시각이다.
    private LocalDateTime approvedAt;

    // 거절 사유이다.
    private String rejectReason;

    // 취소 사유이다.
    private GxReservationCancelReason cancelReason;

    // 취소 시각이다.
    private LocalDateTime cancelledAt;

    // 신청 시각이다.
    private LocalDateTime createdAt;

    // 프로그램의 현재 확정 예약 인원이다.
    private Long confirmedCount;

    // 프로그램 최대 수용 인원이다.
    private Integer maxCount;
}
