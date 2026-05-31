package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 방문차량 상세 조회 응답 DTO이다.
@Getter
@Builder
public class AdminVisitorVehicleDetailRes {

    // 방문차량 ID이다.
    private Long visitorVehicleId;

    // 세대 ID이다.
    private Long householdId;

    // 등록 사용자 ID이다.
    private Long userId;

    // 등록자 이름이다.
    private String residentName;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 차량 번호이다.
    private String licensePlate;

    // 방문자 이름이다.
    private String visitorName;

    // 연락처이다.
    private String phone;

    // 방문 목적이다.
    private String visitPurpose;

    // 방문 예정일이다.
    private LocalDate visitDate;

    // 입차 예정 시각이다.
    private LocalTime startTime;

    // 출차 예정 시각이다.
    private LocalTime endTime;

    // 처리 상태이다.
    private VisitorVehicleStatus status;

    // 재등록 원본 ID이다.
    private Long sourceId;

    // 생성 시각이다.
    private LocalDateTime createdAt;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
