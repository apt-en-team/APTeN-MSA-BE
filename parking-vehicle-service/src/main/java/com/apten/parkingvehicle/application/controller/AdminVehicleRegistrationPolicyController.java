package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VehicleRegistrationPolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VehicleRegistrationPolicyGetRes;
import com.apten.parkingvehicle.application.model.response.VehicleRegistrationPolicyPutRes;
import com.apten.parkingvehicle.application.service.ParkingPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 입주민 차량 등록 한도 정책 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/vehicle-registration-policies")
public class AdminVehicleRegistrationPolicyController {

    // 차량 등록 한도 정책 응용 서비스이다.
    private final ParkingPolicyService parkingPolicyService;

    // 차량 등록 한도 정책 설정
    @PutMapping
    public ResultResponse<VehicleRegistrationPolicyPutRes> updateVehicleRegistrationPolicy(
            @RequestParam Long complexId,
            @RequestBody VehicleRegistrationPolicyPutReq request
    ) {
        return ResultResponse.success("차량 등록 한도 정책 설정 성공", parkingPolicyService.updateVehicleRegistrationPolicy(complexId, request));
    }

    // 차량 등록 한도 정책 조회
    @GetMapping
    public ResultResponse<VehicleRegistrationPolicyGetRes> getVehicleRegistrationPolicy(@RequestParam Long complexId) {
        return ResultResponse.success("차량 등록 한도 정책 조회 성공", parkingPolicyService.getVehicleRegistrationPolicy(complexId));
    }
}
