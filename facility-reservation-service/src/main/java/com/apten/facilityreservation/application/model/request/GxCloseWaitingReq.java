package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 모집 마감 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxCloseWaitingReq {

    // 대기자 일괄 거절 사유이다. null이면 기본 메시지를 사용한다.
    private String rejectReason;
}
