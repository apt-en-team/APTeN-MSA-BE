package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.ParkingStatisticsUnit;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 통계 조회 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatisticsSearchReq {
    private LocalDate fromDate;
    private LocalDate toDate;
    private ParkingStatisticsUnit unit;
}
