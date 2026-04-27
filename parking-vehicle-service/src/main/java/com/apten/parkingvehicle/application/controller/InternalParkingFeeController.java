package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VehicleFeeCalculateReq;
import com.apten.parkingvehicle.application.model.request.VisitorFeeCalculateReq;
import com.apten.parkingvehicle.application.model.response.VehicleFeeCalculateRes;
import com.apten.parkingvehicle.application.model.response.VisitorFeeCalculateRes;
import com.apten.parkingvehicle.application.service.ParkingFeeCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 내부 비용 산정 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalParkingFeeController {

    // 비용 산정 응용 서비스이다.
    private final ParkingFeeCalculationService parkingFeeCalculationService;

    //차량 비용 산정 및 발행 API-333
    @PostMapping("/vehicle-fees/calculate")
    public ResultResponse<VehicleFeeCalculateRes> calculateVehicleFees(@RequestBody VehicleFeeCalculateReq request) {
        return ResultResponse.success("차량 비용 산정 실행 성공", parkingFeeCalculationService.calculateVehicleFees(request));
    }

    //방문차량 비용 산정 및 발행 API-334
    @PostMapping("/visitor-fees/calculate")
    public ResultResponse<VisitorFeeCalculateRes> calculateVisitorFees(@RequestBody VisitorFeeCalculateReq request) {
        return ResultResponse.success("방문차량 비용 산정 실행 성공", parkingFeeCalculationService.calculateVisitorFees(request));
    }
}
