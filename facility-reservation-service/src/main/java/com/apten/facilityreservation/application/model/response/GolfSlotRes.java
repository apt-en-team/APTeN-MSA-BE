package com.apten.facilityreservation.application.model.response;

import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 골프 슬롯 단건 현황 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GolfSlotRes {
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Integer> reservedSeats;
    private Integer availableCount;
}
