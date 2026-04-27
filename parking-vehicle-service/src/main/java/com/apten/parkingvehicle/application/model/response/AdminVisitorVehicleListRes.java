package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 방문 예정 차량 조회 항목 DTO이다.
@Getter
@Builder
public class AdminVisitorVehicleListRes {

    // 방문차량 ID이다.
    private Long visitorVehicleId;

    // 차량 번호이다.
    private String licensePlate;

    // 방문자 이름이다.
    private String visitorName;

    // 연락처이다.
    private String phone;

    // 방문 예정일이다.
    private LocalDate visitDate;

    // 입차 예정 시각이다.
    private LocalTime startTime;

    // 출차 예정 시각이다.
    private LocalTime endTime;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 상태이다.
    private VisitorVehicleStatus status;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
