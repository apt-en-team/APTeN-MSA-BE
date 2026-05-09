package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 차량 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVehicleListReq {

    // 검색 키워드이다.
    private String keyword;

    // 상태 필터이다.
    private VehicleStatus status;

    // 동 필터이다.
    private String building;

    // 호 필터이다.
    private String unit;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
