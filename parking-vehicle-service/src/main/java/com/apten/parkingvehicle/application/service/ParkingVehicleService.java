package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.mapper.ParkingVehicleDtoMapper;
import com.apten.parkingvehicle.application.model.dto.ParkingVehicleDto;
import com.apten.parkingvehicle.application.model.response.ParkingVehicleBaseResponse;
import com.apten.parkingvehicle.domain.entity.ParkingVehicle;
import com.apten.parkingvehicle.domain.enums.ParkingVehicleStatus;
import com.apten.parkingvehicle.domain.repository.ParkingVehicleRepository;
import com.apten.parkingvehicle.infrastructure.mapper.ParkingVehicleQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// parking-vehicle-service 유스케이스를 조합할 기본 서비스 클래스
// JPA와 MyBatis를 함께 사용하는 위치가 application/service라는 점을 구조로 보여준다
@Service
@RequiredArgsConstructor
public class ParkingVehicleService {

    // 단건 저장과 기본 조회는 JPA Repository가 맡는다
    private final ParkingVehicleRepository parkingVehicleRepository;

    // 복잡 조회가 필요해지면 MyBatis 매퍼를 이 계층에서만 사용한다
    private final ObjectProvider<ParkingVehicleQueryMapper> parkingVehicleQueryMapperProvider;

    // 요청 DTO와 응답 DTO 변환은 전용 매퍼에 맡긴다
    private final ParkingVehicleDtoMapper parkingVehicleDtoMapper;

    // 컨트롤러가 바로 연결할 수 있는 최소 응답 형태를 반환한다
    public ParkingVehicleBaseResponse getParkingVehicleTemplate() {
        ParkingVehicle parkingVehicle = ParkingVehicle.builder()
                .id(1L)
                .vehicleNumber("00가0000")
                .status(ParkingVehicleStatus.ACTIVE)
                .build();

        ParkingVehicleDto parkingVehicleDto = parkingVehicleDtoMapper.toDto(parkingVehicle);
        return parkingVehicleDtoMapper.toResponse(parkingVehicleDto);
    }
}
