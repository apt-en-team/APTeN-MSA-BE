package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 이용 비용 발행 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityFeePublishReq {

    // 단지 ID이다.
    private Long complexId;

    // 이용 연도이다.
    private Integer usageYear;

    // 이용 월이다.
    private Integer usageMonth;
}
