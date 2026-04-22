package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 주차 로그 목록 페이지 응답 DTO
@Getter
@Builder
public class ParkingLogGetPageRes {
    private PageResponse<ParkingLogRes> page;

    public static ParkingLogGetPageRes empty(int page, int size) {
        return ParkingLogGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
