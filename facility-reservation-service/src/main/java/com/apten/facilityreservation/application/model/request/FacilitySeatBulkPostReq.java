package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 좌석 일괄 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySeatBulkPostReq {

    // 좌석 라벨 접두어이다.
    private String prefix;

    // 시작 좌석 번호이다.
    private Integer startNo;

    // 종료 좌석 번호이다.
    private Integer endNo;

    // 좌석명 패턴이다.
    private String seatNamePattern;

    // 활성 여부이다.
    private Boolean isActive;
}
