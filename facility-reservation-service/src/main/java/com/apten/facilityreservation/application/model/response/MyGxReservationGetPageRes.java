package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 내 GX 예약 목록 페이지 응답 DTO
@Getter
@Builder
public class MyGxReservationGetPageRes {
    private PageResponse<MyGxReservationGetRes> page;

    public static MyGxReservationGetPageRes empty(int page, int size) {
        return MyGxReservationGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
