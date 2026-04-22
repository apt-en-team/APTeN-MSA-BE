package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 관리자 차량 목록 페이지 응답 DTO
@Getter
@Builder
public class VehicleAdminGetPageRes {
    private PageResponse<VehicleAdminGetRes> page;

    public static VehicleAdminGetPageRes empty(int page, int size) {
        return VehicleAdminGetPageRes.builder()
                .page(PageResponse.empty(page, size))
                .build();
    }
}
