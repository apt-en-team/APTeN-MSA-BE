package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.ParkingEntryType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 입출차 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLogCreateReq {

    // 주차층 ID이다.
    private Long parkingFloorId;

    // 차량 번호이다.
    private String licensePlate;

    // 입출차 구분이다.
    private ParkingEntryType entryType;

    // 기록 시각이다.
    private LocalDateTime loggedAt;

    // 메모이다.
    private String memo;
}
