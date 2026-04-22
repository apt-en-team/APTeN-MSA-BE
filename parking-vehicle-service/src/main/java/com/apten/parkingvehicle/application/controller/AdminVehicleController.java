package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VehicleAdminSearchReq;
import com.apten.parkingvehicle.application.model.request.VehicleRejectReq;
import com.apten.parkingvehicle.application.model.response.VehicleAdminGetPageRes;
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

    @PatchMapping("/api/admin/vehicles/{vehicleUid}/approve")
    public ResultResponse<VehicleApproveRes> approveVehicle(@PathVariable String vehicleUid) {
        return ResultResponse.success("차량 승인 성공", vehicleService.approveVehicle(vehicleUid));
    }

    @PatchMapping("/api/admin/vehicles/{vehicleUid}/reject")
    public ResultResponse<VehicleRejectRes> rejectVehicle(
            @PathVariable String vehicleUid,
            @RequestBody VehicleRejectReq request
    ) {
        return ResultResponse.success("차량 거절 성공", vehicleService.rejectVehicle(vehicleUid, request));
    }

    @GetMapping("/api/admin/vehicles")
    public ResultResponse<VehicleAdminGetPageRes> getAdminVehicleList(@ModelAttribute VehicleAdminSearchReq request) {
        return ResultResponse.success("전체 차량 목록 조회 성공", vehicleService.getAdminVehicleList(request));
    }
}
