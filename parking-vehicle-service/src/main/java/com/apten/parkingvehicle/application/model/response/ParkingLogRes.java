package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 로그 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLogRes {
    private Long parkingLogId;
    private String parkingLogUid;
    private String floorName;
    private String licensePlate;
    private String entryType;
    private LocalDateTime loggedAt;
    private String memo;
}
