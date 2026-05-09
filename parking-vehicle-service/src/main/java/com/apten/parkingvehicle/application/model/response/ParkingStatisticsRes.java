package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 통계 차트 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatisticsRes {

    // 차트 단위이다.
    private String chartUnit;

    // 라벨 목록이다.
    private List<String> labels;

    // 입차 수 목록이다.
    private List<Integer> inCount;

    // 출차 수 목록이다.
    private List<Integer> outCount;

    // 평균 점유율이다.
    private BigDecimal averageOccupancyRate;
}
