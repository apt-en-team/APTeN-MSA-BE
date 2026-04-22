package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.response.ParkingVehicleBaseResponse;
import com.apten.parkingvehicle.application.service.ParkingVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// parking-vehicle-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 차량 등록과 조회 API는 이후 이 계층에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/parking-vehicles")
public class ParkingVehicleController {

    // 차량 응용 계층 진입점
    private final ParkingVehicleService parkingVehicleService;

    // 공통 응답 포맷과 기본 라우팅 연결이 정상인지 확인하는 최소 엔드포인트
    @GetMapping("/template")
    public ResultResponse<ParkingVehicleBaseResponse> getParkingVehicleTemplate() {
        return ResultResponse.success("parking vehicle template ready", parkingVehicleService.getParkingVehicleTemplate());
    }
}
