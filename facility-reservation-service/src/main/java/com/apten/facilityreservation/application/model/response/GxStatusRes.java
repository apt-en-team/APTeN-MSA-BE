package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// GX 현황 조회 응답 DTO이다.
@Getter
@Builder
public class GxStatusRes {

    // 프로그램 ID이다.
    private Long programId;

    // 확정 인원이다.
    private Integer confirmedCount;

    // 대기 인원이다.
    private Integer waitingCount;

    // 거절 인원이다.
    private Integer rejectedCount;

    // 취소 인원이다.
    private Integer cancelledCount;
}
