package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleExpireRes;
import com.apten.parkingvehicle.application.service.VisitorVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 방문차량 스케줄러 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/visitor-vehicles")
public class InternalVisitorVehicleController {

    // 방문차량 응용 서비스이다.
    private final VisitorVehicleService visitorVehicleService;

    //방문차량 자동 만료 API-319
    @PostMapping("/expire")
    public ResultResponse<VisitorVehicleExpireRes> expireVisitorVehicles() {
        return ResultResponse.success("방문차량 자동 만료 실행 성공", visitorVehicleService.expireVisitorVehicles());
    }
}
