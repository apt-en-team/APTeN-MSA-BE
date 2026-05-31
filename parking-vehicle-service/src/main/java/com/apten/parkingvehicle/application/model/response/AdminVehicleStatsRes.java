package com.apten.parkingvehicle.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 차량 상태별 통계 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVehicleStatsRes {

    // 전체 차량 건수이다.
    private long total;

    // 승인 대기 건수이다.
    private long pending;

    // 승인 완료 건수이다.
    private long approved;

    // 거부 건수이다.
    private long rejected;
}
