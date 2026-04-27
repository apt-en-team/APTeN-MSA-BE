package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 이용 비용 산정 응답 DTO이다.
@Getter
@Builder
public class FacilityFeeCalculateRes {

    // 단지 ID이다.
    private Long complexId;

    // 이용 연도이다.
    private Integer usageYear;

    // 이용 월이다.
    private Integer usageMonth;

    // 산정 건수이다.
    private Integer calculatedCount;

    // 산정 시각이다.
    private LocalDateTime calculatedAt;
}
