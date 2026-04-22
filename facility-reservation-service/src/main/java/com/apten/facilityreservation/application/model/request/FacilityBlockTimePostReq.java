package com.apten.facilityreservation.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 차단 시간 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityBlockTimePostReq {
    private LocalDate blockDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
}
