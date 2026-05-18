package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 시설 좌석 일괄 등록 응답 DTO이다.
@Getter
@Builder
public class FacilitySeatBulkPostRes {

    // 생성 건수이다.
    private Integer createdCount;

    // 첫 번째 좌석 번호이다.
    private Integer firstSeatNo;

    // 마지막 좌석 번호이다.
    private Integer lastSeatNo;
}
