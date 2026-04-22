package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 골프 좌석 현황 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GolfStatusRes {
    private LocalDate targetDate;
    private List<GolfSlotRes> slots;
}
