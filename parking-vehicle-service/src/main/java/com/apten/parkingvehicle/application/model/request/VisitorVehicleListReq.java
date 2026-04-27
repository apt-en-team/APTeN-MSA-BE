package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehicleListReq {

    // 상태 필터이다.
    private VisitorVehicleStatus status;

    // 조회 시작일이다.
    private LocalDate fromDate;

    // 조회 종료일이다.
    private LocalDate toDate;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
