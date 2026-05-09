package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import lombok.Builder;
import lombok.Getter;

// GX 대기 순번 조회 응답 DTO이다.
@Getter
@Builder
public class GxWaitingRes {

    // GX 예약 ID이다.
    private Long gxReservationId;

    // 프로그램 ID이다.
    private Long programId;

    // 대기 순번이다.
    private Integer waitNo;

    // 상태이다.
    private GxReservationStatus status;
}
