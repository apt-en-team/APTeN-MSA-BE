package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 내 예약 목록 페이지 응답 DTO
@Getter
@Builder
public class MyReservationGetPageRes {
    private PageResponse<MyReservationGetRes> page;

    public static MyReservationGetPageRes empty(int page, int size) {
        return MyReservationGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
