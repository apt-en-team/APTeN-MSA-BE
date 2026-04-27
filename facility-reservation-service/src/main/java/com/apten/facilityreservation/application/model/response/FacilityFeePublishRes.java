package com.apten.facilityreservation.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 이용 비용 발행 응답 DTO이다.
@Getter
@Builder
public class FacilityFeePublishRes {

    // 단지 ID이다.
    private Long complexId;

    // 이용 연도이다.
    private Integer usageYear;

    // 이용 월이다.
    private Integer usageMonth;

    // 발행 건수이다.
    private Integer publishedCount;

    // 발행 시각이다.
    private LocalDateTime publishedAt;
}
