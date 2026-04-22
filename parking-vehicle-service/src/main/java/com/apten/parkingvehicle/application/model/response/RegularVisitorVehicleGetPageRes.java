package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 고정 방문차량 목록 페이지 응답 DTO
@Getter
@Builder
public class RegularVisitorVehicleGetPageRes {
    private PageResponse<RegularVisitorVehicleRes> page;

    public static RegularVisitorVehicleGetPageRes empty(int page, int size) {
        return RegularVisitorVehicleGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
