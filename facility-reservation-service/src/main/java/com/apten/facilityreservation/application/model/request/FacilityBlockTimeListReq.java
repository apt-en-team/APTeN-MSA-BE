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

    // 차단일 필터이다.
    private LocalDate blockDate;
}
