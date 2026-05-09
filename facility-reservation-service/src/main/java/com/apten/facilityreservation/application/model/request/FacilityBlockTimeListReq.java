package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 차단 시간 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityBlockTimeListReq {

    // 조회 시작일 필터이다.
    private LocalDate fromDate;

    // 조회 종료일 필터이다.
    private LocalDate toDate;

    // 활성 여부 필터이다.
    private Boolean isActive;
}
