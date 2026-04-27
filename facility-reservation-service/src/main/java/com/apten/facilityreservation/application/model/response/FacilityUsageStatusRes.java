package com.apten.facilityreservation.application.model.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 이용 현황 조회 응답 DTO이다.
@Getter
@Builder
public class FacilityUsageStatusRes {

    // 단지 ID이다.
    private Long complexId;

    // 조회 기준일이다.
    private LocalDate targetDate;

    // 시설별 현황 목록이다.
    private List<Item> items;

    // 시설별 현황 내부 DTO이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        // 시설 ID이다.
        private Long facilityId;

        // 시설명이다.
        private String facilityName;

        // 예약 수이다.
        private Integer reservedCount;
    }
}
