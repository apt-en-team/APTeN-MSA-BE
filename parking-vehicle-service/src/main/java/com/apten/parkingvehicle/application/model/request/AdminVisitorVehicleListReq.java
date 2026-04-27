package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.ParkingTargetDateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 방문 예정 차량 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVisitorVehicleListReq {

    // 조회 날짜 구분이다.
    private ParkingTargetDateType targetDateType;

    // 검색 키워드이다.
    private String keyword;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
