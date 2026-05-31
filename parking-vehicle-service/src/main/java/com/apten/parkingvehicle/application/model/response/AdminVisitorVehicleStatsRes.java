package com.apten.parkingvehicle.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 방문차량 통계 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVisitorVehicleStatsRes {

    // 오늘 방문 예정 건수이다.
    private long todayScheduled;

    // 앞으로 올 방문 건수이다.
    private long upcoming;

    // 이번 달 전체 건수이다.
    private long monthTotal;
}
