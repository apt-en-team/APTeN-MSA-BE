package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 재등록 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehicleReRegisterRes {

    // 방문차량 ID이다.
    private Long visitorVehicleId;

    // 재등록 원본 방문차량 ID이다.
    private Long sourceVisitorVehicleId;

    // 차량 번호이다.
    private String licensePlate;

    // 방문 예정일이다.
    private LocalDate visitDate;

    // 입차 예정 시각이다.
    private LocalTime startTime;

    // 출차 예정 시각이다.
    private LocalTime endTime;

    // 처리 상태이다.
    private VisitorVehicleStatus status;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
