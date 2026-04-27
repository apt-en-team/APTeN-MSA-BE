package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// GX 최소 인원 검증 응답 DTO이다.
@Getter
@Builder
public class GxMinimumCheckRes {

    // 프로그램 ID이다.
    private Long programId;

    // 최소 인원이다.
    private Integer minCount;

    // 현재 확정 인원이다.
    private Integer currentCount;

    // 최소 인원 충족 여부이다.
    private Boolean satisfied;
}
