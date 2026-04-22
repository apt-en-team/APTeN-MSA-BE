package com.apten.parkingvehicle.application.model.dto;

import lombok.Builder;
import lombok.Getter;

// parking-vehicle-service 내부 계층 간 전달에 사용할 최소 DTO
// 요청 모델과 엔티티를 직접 결합하지 않기 위한 중간 전달 객체다
@Getter
@Builder
public class ParkingVehicleDto {
}
