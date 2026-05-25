package com.apten.facilityreservation.application.model.request;

import lombok.Getter;
import lombok.Setter;

// 입주민 내 예약 통합 목록 조회 요청 DTO이다.
// @ModelAttribute 바인딩을 위해 @Setter가 필요하다.
@Getter
@Setter
public class MyUnifiedReservationReq {

    // 탭 구분 — "UPCOMING"(예정) 또는 "PAST"(지난). null이면 전체 조회.
    private String phase;

    // 페이지 번호 (0-based)
    private Integer page = 0;

    // 페이지 크기
    private Integer size = 10;
}
