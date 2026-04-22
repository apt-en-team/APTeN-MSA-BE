package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 내 방문차량 목록 페이지 응답 DTO
@Getter
@Builder
public class VisitorVehicleGetPageRes {
    private PageResponse<VisitorVehicleRes> page;

    public static VisitorVehicleGetPageRes empty(int page, int size) {
        return VisitorVehicleGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
