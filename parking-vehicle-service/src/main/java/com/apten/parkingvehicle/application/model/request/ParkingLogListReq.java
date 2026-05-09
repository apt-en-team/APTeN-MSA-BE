package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.ParkingEntryType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 입출차 기록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLogListReq {

    // 시작일이다.
    private LocalDate fromDate;

    // 종료일이다.
    private LocalDate toDate;

    // 차량 번호이다.
    private String licensePlate;

    // 입출차 구분이다.
    private ParkingEntryType entryType;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
