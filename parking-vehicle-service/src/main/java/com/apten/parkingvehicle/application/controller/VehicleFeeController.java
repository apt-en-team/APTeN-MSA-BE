package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.response.VehicleFeeMonthlyGetRes;
import com.apten.parkingvehicle.application.service.ParkingFeeCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 입주민 차량 월 과금 조회 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles/fees")
public class VehicleFeeController {

    // 비용 산정/조회 응용 서비스
    private final ParkingFeeCalculationService parkingFeeCalculationService;

    // 본인 세대의 특정 월 차량 과금 단건 조회
    @GetMapping("/monthly")
    public ResultResponse<VehicleFeeMonthlyGetRes> getMonthlyFee(
            @RequestParam("yearMonth") String yearMonth,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success(
                "차량 월 과금 조회 성공",
                parkingFeeCalculationService.getResidentVehicleMonthlyFee(yearMonth, userId, userRole, complexId)
        );
    }
}
