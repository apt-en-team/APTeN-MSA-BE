package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.ReservationStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 예약 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationListReq {

    // 단지 ID이다.
    private Long complexId;

    // 시설 ID 필터이다.
    private Long facilityId;

    // 예약일 필터이다.
    private LocalDate reservationDate;

    // 상태 필터이다.
    private ReservationStatus status;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
