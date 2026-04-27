package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 비용 산정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorFeeCalculateReq {

    // 단지 ID이다.
    private Long complexId;

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;
}
