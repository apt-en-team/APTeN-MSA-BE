package com.apten.parkingvehicle.application.model.dto;

import com.apten.parkingvehicle.domain.enums.ParkingVehicleStatus;
import lombok.Builder;
import lombok.Getter;

// parking-vehicle-service 내부 계층 간 전달에 사용할 최소 DTO
// 요청 모델과 엔티티를 직접 섞지 않기 위한 중간 전달 객체다
@Getter
@Builder
public class ParkingVehicleDto {

    // 차량 식별자
    private final Long id;

    // 차량 번호
    private final String vehicleNumber;

    // 차량 상태
    private final ParkingVehicleStatus status;
}
