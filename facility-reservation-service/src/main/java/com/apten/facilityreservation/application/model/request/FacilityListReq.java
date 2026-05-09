package com.apten.facilityreservation.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 시설 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityListReq {

    // 시설 타입 ID 필터이다.
    private Long typeId;

    // 예약 방식 필터이다.
    private com.apten.facilityreservation.domain.enums.ReservationType reservationType;

    // 활성 여부 필터이다.
    private Boolean isActive;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
