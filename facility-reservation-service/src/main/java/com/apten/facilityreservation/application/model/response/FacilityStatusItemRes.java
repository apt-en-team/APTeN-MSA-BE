package com.apten.facilityreservation.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 시설별 예약 현황 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityStatusItemRes {
    private String facilityUid;
    private String facilityName;
    private Integer reservedCount;
    private Integer waitingCount;
    private Integer availableCount;
}
