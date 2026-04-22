package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.ParkingVehicleStatus;
import lombok.Builder;
import lombok.Getter;

// parking-vehicle-service 응답 모델의 공통 시작점으로 두는 최소 응답 DTO
// 공통 ResultResponse 안에 담길 차량 전용 응답 객체가 이 패키지에 모이게 된다
@Getter
@Builder
public class ParkingVehicleBaseResponse {

    // 차량 식별자
    private final Long id;

    // 차량 번호
    private final String vehicleNumber;

    // 차량 상태
    private final ParkingVehicleStatus status;
}
