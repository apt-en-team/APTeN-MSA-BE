package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.ParkingEntryType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 주차 입출차 등록 응답 DTO이다.
@Getter
@Builder
public class ParkingLogCreateRes {

    // 주차 로그 ID이다.
    private Long parkingLogId;

    // 주차층 ID이다.
    private Long parkingFloorId;

    // 차량 번호이다.
    private String licensePlate;

    // 입출차 구분이다.
    private ParkingEntryType entryType;

    // 기록 시각이다.
    private LocalDateTime loggedAt;
}
