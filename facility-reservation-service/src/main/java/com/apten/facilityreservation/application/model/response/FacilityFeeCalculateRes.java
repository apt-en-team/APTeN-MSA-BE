package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 이용 비용 산정 응답 DTO이다.
@Getter
@Builder
public class FacilityFeeCalculateRes {

    // 이용 연도이다.
    private Integer usageYear;

    // 이용 월이다.
    private Integer usageMonth;

    // 처리 건수이다.
    private Integer processedCount;

    // 산정 시각이다.
    private LocalDateTime calculatedAt;
}
