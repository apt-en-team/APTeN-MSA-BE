package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

// 시설 이용 현황 조회 응답 DTO이다.
@Getter
@Builder
public class FacilityUsageStatusRes {

    // 시설 ID이다.
    private Long facilityId;

    // 조회 기준일이다.
    private LocalDate targetDate;

    // 예약 건수이다.
    private Integer reservedCount;

    // 완료 건수이다.
    private Integer completedCount;

    // 취소 건수이다.
    private Integer cancelledCount;
}
