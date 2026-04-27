package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// GX 예약 신청 응답 DTO이다.
@Getter
@Builder
public class GxReservationPostRes {

    // GX 예약 ID이다.
    private Long gxReservationId;

    // 프로그램 ID이다.
    private Long programId;

    // 상태이다.
    private GxReservationStatus status;

    // 대기 순번이다.
    private Integer waitNo;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
