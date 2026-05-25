package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 입주민 내 예약 목록 통합 응답 DTO — 일반 시설 예약과 GX 예약을 하나의 형태로 반환한다.
@Getter
@Builder
public class MyUnifiedReservationRes {

    // 예약 종류 구분자 — "FACILITY" 또는 "GX"
    private String type;

    // 일반 시설 예약 ID (type=FACILITY 일 때 사용)
    private Long reservationId;

    // GX 예약 ID (type=GX 일 때 사용)
    private Long gxReservationId;

    // GX 프로그램 ID (type=GX 일 때 사용)
    private Long programId;

    // 표시명 — 시설 예약은 시설명, GX 예약은 프로그램명
    private String name;

    // 예약 기준일 — 시설 예약은 reservationDate, GX 예약은 startDate
    private LocalDate reservationDate;

    // GX 종료일 (type=GX 일 때만 존재)
    private LocalDate endDate;

    // 시작 시각
    private LocalTime startTime;

    // 종료 시각
    private LocalTime endTime;

    // GX 운영 요일 (type=GX 일 때만 존재)
    private String daysOfWeek;

    // 좌석 번호 (type=FACILITY, 좌석형 예약일 때만 존재)
    private Integer seatNo;

    // 상태 코드 문자열 — 각 enum의 code 값
    private String status;

    // GX 대기 순번 (type=GX, 대기 상태일 때만 존재)
    private Integer waitNo;

    // 생성 시각 — 정렬 보조 키
    private LocalDateTime createdAt;
}
