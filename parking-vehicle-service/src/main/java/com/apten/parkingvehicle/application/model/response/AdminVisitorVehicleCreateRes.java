package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 방문차량 등록 응답 DTO이다.
@Getter
@Builder
public class AdminVisitorVehicleCreateRes {

    // 방문차량 ID이다.
    private Long visitorVehicleId;

    // 세대 ID이다.
    private Long householdId;

    // 차량 번호이다.
    private String licensePlate;

    // 방문 예정일이다.
    private LocalDate visitDate;

    // 상태이다.
    private VisitorVehicleStatus status;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
