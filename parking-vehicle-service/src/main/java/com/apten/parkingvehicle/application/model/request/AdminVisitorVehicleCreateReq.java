package com.apten.parkingvehicle.application.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 방문차량 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVisitorVehicleCreateReq {

    // 세대 ID이다.
    private Long householdId;

    // 차량 번호이다.
    private String licensePlate;

    // 방문자 이름이다.
    private String visitorName;

    // 방문자 연락처이다.
    private String phone;

    // 방문 예정일이다.
    private LocalDate visitDate;

    // 입차 예정 시각이다.
    private LocalTime startTime;

    // 출차 예정 시각이다.
    private LocalTime endTime;

    // 관리자 메모이다.
    private String memo;
}
