package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 이용 현황 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityUsageStatusReq {

    // 단지 ID이다.
    private Long complexId;

    // 조회 기준일이다.
    private LocalDate targetDate;
}
