package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 프로그램 취소 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxProgramCancelReq {

    // 취소 사유이다.
    private String reason;
}
