package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 좌석 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySeatPostReq {

    // 좌석 번호이다.
    private Integer seatNo;

    // 좌석명이다.
    private String seatName;

    // 정렬 순서이다.
    private Integer sortOrder;

    // 활성 여부이다.
    private Boolean isActive;
}
