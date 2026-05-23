package com.apten.parkingvehicle.application.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 센서 일괄 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSensorBulkPostReq {

    // 소속 주차 구역 ID이다. (일괄 등록은 단일 zone 한정)
    private Long zoneId;

    // 일괄 등록할 센서 항목 목록이다.
    private List<Item> items;

    // 일괄 등록 항목 단위 데이터이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        // 자리 번호이다.
        private String spotNumber;

        // 센서 식별 코드이다.
        private String sensorCode;

        // 자리 부가 설명이다. (NULL 가능)
        private String description;
    }
}
