package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import com.apten.parkingvehicle.domain.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 차량 상세 조회 응답 DTO이다.
@Getter
@Builder
public class AdminVehicleDetailRes {

    // 차량 ID이다.
    private Long vehicleId;

    // 세대 ID이다.
    private Long householdId;

    // 사용자 ID이다.
    private Long userId;

    // 입주민 이름이다.
    private String residentName;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 차량 번호이다.
    private String licensePlate;

    // 모델명이다.
    private String modelName;

    // 차량 종류이다.
    private VehicleType vehicleType;

    // 차량 상태이다.
    private VehicleStatus status;

    // 대표 차량 여부이다.
    private Boolean isPrimary;

    // 승인 시각이다.
    private LocalDateTime approvedAt;

    // 거절 사유이다.
    private String rejectReason;

    // 생성 시각이다.
    private LocalDateTime createdAt;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
