package com.apten.parkingvehicle.application.mapper;

import com.apten.parkingvehicle.application.model.dto.ParkingVehicleDto;
import com.apten.parkingvehicle.application.model.request.ParkingVehicleBaseRequest;
import com.apten.parkingvehicle.application.model.response.ParkingVehicleBaseResponse;
import com.apten.parkingvehicle.domain.entity.ParkingVehicle;
import org.springframework.stereotype.Component;

// parking-vehicle-service의 요청, 응답, 내부 DTO 변환을 맡는 전용 매퍼
// 서비스가 변환 코드까지 떠안지 않도록 application 계층 안에서 역할을 분리한다
@Component
public class ParkingVehicleDtoMapper {

    // 요청 DTO를 저장 전 엔티티 형태로 옮긴다
    public ParkingVehicle toEntity(ParkingVehicleBaseRequest request) {
        return ParkingVehicle.builder()
                .vehicleNumber(request.getVehicleNumber())
                .status(request.getStatus())
                .build();
    }

    // 엔티티를 서비스 내부 전달용 DTO로 바꾼다
    public ParkingVehicleDto toDto(ParkingVehicle parkingVehicle) {
        return ParkingVehicleDto.builder()
                .id(parkingVehicle.getId())
                .vehicleNumber(parkingVehicle.getVehicleNumber())
                .status(parkingVehicle.getStatus())
                .build();
    }

    // 내부 DTO를 외부 응답 모델로 바꾼다
    public ParkingVehicleBaseResponse toResponse(ParkingVehicleDto parkingVehicleDto) {
        return ParkingVehicleBaseResponse.builder()
                .id(parkingVehicleDto.getId())
                .vehicleNumber(parkingVehicleDto.getVehicleNumber())
                .status(parkingVehicleDto.getStatus())
                .build();
    }
}
