package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VehiclePolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyListRes;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyPutRes;
import com.apten.parkingvehicle.application.service.ParkingPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 차량 정책 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/vehicle-policies")
public class AdminVehiclePolicyController {

    // 차량 정책 응용 서비스이다.
    private final ParkingPolicyService parkingPolicyService;

    // 차량 정책 설정
    @PutMapping
    public ResultResponse<VehiclePolicyPutRes> updateVehiclePolicy(
            @RequestBody VehiclePolicyPutReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("차량 정책 설정 성공", parkingPolicyService.updateVehiclePolicy(request, userRole, complexId, selectedComplexId));
    }

    // 차량 정책 조회
    @GetMapping
    public ResultResponse<VehiclePolicyListRes> getVehiclePolicies(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("차량 정책 조회 성공", parkingPolicyService.getVehiclePolicies(userRole, complexId, selectedComplexId));
    }
}
