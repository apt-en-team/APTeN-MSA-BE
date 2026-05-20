package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.ReservationKind;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

// 관리자 예약 통합 개요 조회 요청 DTO이다.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationOverviewReq {

    // 예약 종류 필터이다. null이면 전체 조회한다.
    private ReservationKind reservationKind;

    // 시설 ID 필터이다.
    private Long facilityId;

    // 통합 상태 필터이다. CONFIRMED / CANCELLED / COMPLETED / WAITING
    private String status;

    // 예약자명 Like 검색이다.
    private String residentName;

    // 시설명/프로그램명 Like 검색이다.
    private String facilityName;

    // 예약일 필터이다. 일반 예약 전용이다.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reservationDate;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
