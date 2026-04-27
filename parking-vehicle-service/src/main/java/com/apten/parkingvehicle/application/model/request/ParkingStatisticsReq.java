package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.ParkingStatisticsUnit;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 통계 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatisticsReq {

    // 시작일이다.
    private LocalDate fromDate;

    // 종료일이다.
    private LocalDate toDate;

    // 집계 단위이다.
    private ParkingStatisticsUnit unit;
}
