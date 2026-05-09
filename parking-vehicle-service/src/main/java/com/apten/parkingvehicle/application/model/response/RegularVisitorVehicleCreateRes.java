package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 고정 방문차량 등록 응답 DTO이다.
@Getter
@Builder
public class RegularVisitorVehicleCreateRes {

    // 고정 방문차량 ID이다.
    private Long regularVisitorVehicleId;

    // 차량 번호이다.
    private String licensePlate;

    // 방문자 이름이다.
    private String visitorName;

    // 연락처이다.
    private String phone;

    // 시작일이다.
    private LocalDate startDate;

    // 종료일이다.
    private LocalDate endDate;

    // 활성 여부이다.
    private Boolean isActive;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
