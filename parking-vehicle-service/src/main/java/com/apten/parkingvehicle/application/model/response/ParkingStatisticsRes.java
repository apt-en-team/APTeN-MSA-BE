package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 통계 차트 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatisticsRes {
    private String chartUnit;
    private List<String> labels;
    private List<Integer> inCount;
    private List<Integer> outCount;
    private BigDecimal averageOccupancyRate;
}
