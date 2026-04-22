package com.apten.parkingvehicle.application.model.request;

import com.apten.parkingvehicle.domain.enums.ParkingVehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// parking-vehicle-service 요청 모델의 공통 시작점으로 두는 최소 요청 DTO
// 차량 등록과 수정에서 공통으로 받을 값만 먼저 정리해 둔다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingVehicleBaseRequest {

    // 차량 번호 입력값
    private String vehicleNumber;

    // 차량 상태 입력값
    private ParkingVehicleStatus status;
}
