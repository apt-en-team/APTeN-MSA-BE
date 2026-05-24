package com.apten.facilityreservation.application.model.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 반복 차단 시간 배치 등록 응답 DTO이다.
@Getter
@Builder
public class FacilityBlockTimeBatchPostRes {

    private Long batchId;

    private Long facilityId;

    private int createdCount;

    private LocalDate validFrom;

    private LocalDate validUntil;

    private List<DayOfWeek> daysOfWeek;

    private LocalDateTime createdAt;
}
