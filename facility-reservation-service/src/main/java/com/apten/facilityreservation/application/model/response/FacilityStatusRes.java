package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 시설별 예약 현황 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityStatusRes {
    private String facilityTypeUid;
    private LocalDate targetDate;
    private List<FacilityStatusItemRes> items;
}
