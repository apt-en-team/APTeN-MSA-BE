package com.apten.parkingvehicle.application.model.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 로그 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLogPostReq {
    private String floorUid;
    private String licensePlate;
    private String entryType;
    private LocalDateTime loggedAt;
    private String memo;
}
