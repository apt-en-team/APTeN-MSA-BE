package com.apten.facilityreservation.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 관리자 예약 목록 페이지 응답 DTO
@Getter
@Builder
public class AdminReservationGetPageRes {
    private PageResponse<AdminReservationGetRes> page;

    public static AdminReservationGetPageRes empty(int page, int size) {
        return AdminReservationGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
