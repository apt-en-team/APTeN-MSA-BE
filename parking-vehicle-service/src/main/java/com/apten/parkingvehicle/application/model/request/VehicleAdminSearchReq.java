package com.apten.parkingvehicle.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 차량 목록 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAdminSearchReq {
    private String keyword;
    private String status;
    private String dong;
    private String ho;
    private Integer page;
    private Integer size;
}
