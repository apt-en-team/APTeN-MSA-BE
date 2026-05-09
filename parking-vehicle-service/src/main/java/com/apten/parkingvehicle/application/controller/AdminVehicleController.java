package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.AdminVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VehicleRejectReq;
import com.apten.parkingvehicle.application.model.response.AdminVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.AdminVehicleListRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VehicleApproveRes;
import com.apten.parkingvehicle.application.model.response.VehicleRejectRes;
import com.apten.parkingvehicle.application.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 관리자 차량 승인과 목록 조회 API 진입점
@RestController
@RequiredArgsConstructor
public class AdminVehicleController {

    private final VehicleService vehicleService;

    //차량 승인 API-304
    @PatchMapping("/api/admin/vehicles/{vehicleId}/approve")
    public ResultResponse<VehicleApproveRes> approveVehicle(@PathVariable Long vehicleId) {
        return ResultResponse.success("차량 승인 성공", vehicleService.approveVehicle(vehicleId));
    }

    //차량 거절 API-305
    @PatchMapping("/api/admin/vehicles/{vehicleId}/reject")
    public ResultResponse<VehicleRejectRes> rejectVehicle(
            @PathVariable Long vehicleId,
            @RequestBody VehicleRejectReq request
    ) {
        return ResultResponse.success("차량 거절 성공", vehicleService.rejectVehicle(vehicleId, request));
    }

    //전체 차량 조회 API-307
    @GetMapping("/api/admin/vehicles")
    public ResultResponse<PageResponse<AdminVehicleListRes>> getAdminVehicleList(@ModelAttribute AdminVehicleListReq request) {
        return ResultResponse.success("전체 차량 목록 조회 성공", vehicleService.getAdminVehicleList(request));
    }

    //관리자 차량 상세 조회 API-336
    @GetMapping("/api/admin/vehicles/{vehicleId}")
    public ResultResponse<AdminVehicleDetailRes> getAdminVehicleDetail(@PathVariable Long vehicleId) {
        return ResultResponse.success("관리자 차량 상세 조회 성공", vehicleService.getAdminVehicleDetail(vehicleId));
    }
}
