package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VehiclePolicyPutReq;
import com.apten.parkingvehicle.application.model.request.VisitorPolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyPutRes;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyPutRes;
import com.apten.parkingvehicle.application.service.ParkingPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 차량 정책과 방문차량 정책 설정 API 진입점이다.
// 현재 단계에서는 관리자 컨텍스트 대신 request parameter로 complexId를 받는다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/parking-vehicle/policies")
public class AdminParkingPolicyController {

    // 정책 원본 저장을 담당하는 응용 서비스이다.
    private final ParkingPolicyService parkingPolicyService;

    // 차량 정책 설정 API이다.
    @PutMapping("/vehicle")
    public ResultResponse<VehiclePolicyPutRes> updateVehiclePolicy(
            @RequestParam Long complexId,
            @RequestBody VehiclePolicyPutReq req
    ) {
        return ResultResponse.success(
                "차량 정책 설정 성공",
                parkingPolicyService.updateVehiclePolicy(complexId, req)
        );
    }

    // 방문차량 정책 설정 API이다.
    @PutMapping("/visitor-vehicle")
    public ResultResponse<VisitorPolicyPutRes> updateVisitorPolicy(
            @RequestParam Long complexId,
            @RequestBody VisitorPolicyPutReq req
    ) {
        return ResultResponse.success(
                "방문차량 정책 설정 성공",
                parkingPolicyService.updateVisitorPolicy(complexId, req)
        );
    }
}
