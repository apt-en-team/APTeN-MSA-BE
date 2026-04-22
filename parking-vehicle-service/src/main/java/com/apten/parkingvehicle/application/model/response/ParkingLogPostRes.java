package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 로그 등록 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLogPostRes {
    private Long parkingLogId;
    private String parkingLogUid;
    private String floorUid;
    private String licensePlate;
    private String entryType;
    private LocalDateTime loggedAt;
}
