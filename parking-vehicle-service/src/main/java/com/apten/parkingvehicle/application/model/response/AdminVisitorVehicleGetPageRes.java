package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 관리자 방문 예정 차량 목록 페이지 응답 DTO
@Getter
@Builder
public class AdminVisitorVehicleGetPageRes {
    private PageResponse<AdminVisitorVehicleRes> page;

    public static AdminVisitorVehicleGetPageRes empty(int page, int size) {
        return AdminVisitorVehicleGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
