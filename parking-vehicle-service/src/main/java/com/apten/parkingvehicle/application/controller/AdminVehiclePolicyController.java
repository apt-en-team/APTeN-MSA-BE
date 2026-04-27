package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VehiclePolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyListRes;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyPutRes;
import com.apten.parkingvehicle.application.service.ParkingPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 차량 정책 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/vehicle-policies")
public class AdminVehiclePolicyController {

    // 차량 정책 응용 서비스이다.
    private final ParkingPolicyService parkingPolicyService;

    //차량 정책 설정 API-309
    @PutMapping
    public ResultResponse<VehiclePolicyPutRes> updateVehiclePolicy(
            @RequestParam Long complexId,
            @RequestBody VehiclePolicyPutReq request
    ) {
        return ResultResponse.success("차량 정책 설정 성공", parkingPolicyService.updateVehiclePolicy(complexId, request));
    }

    //차량 정책 조회 API-310
    @GetMapping
    public ResultResponse<VehiclePolicyListRes> getVehiclePolicies(@RequestParam Long complexId) {
        return ResultResponse.success("차량 정책 조회 성공", parkingPolicyService.getVehiclePolicies(complexId));
    }
}
