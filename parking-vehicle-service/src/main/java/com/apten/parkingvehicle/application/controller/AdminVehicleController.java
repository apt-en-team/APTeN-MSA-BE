package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.AdminVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VehicleRejectReq;
import com.apten.parkingvehicle.application.model.response.AdminVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.AdminVehicleListRes;
import com.apten.parkingvehicle.application.model.response.AdminVehicleLocationRes;
import com.apten.parkingvehicle.application.model.response.AdminVehicleStatsRes;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// 관리자 차량 승인과 목록 조회 API 진입점
@RestController
@RequiredArgsConstructor
public class AdminVehicleController {

    private final VehicleService vehicleService;

    //차량 승인
    @PatchMapping("/api/admin/vehicles/{vehicleId}/approve")
    public ResultResponse<VehicleApproveRes> approveVehicle(
            @PathVariable Long vehicleId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("차량 승인 성공", vehicleService.approveVehicle(vehicleId, userRole, complexId, selectedComplexId));
    }

    //차량 거절
    @PatchMapping("/api/admin/vehicles/{vehicleId}/reject")
    public ResultResponse<VehicleRejectRes> rejectVehicle(
            @PathVariable Long vehicleId,
            @RequestBody VehicleRejectReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("차량 거절 성공", vehicleService.rejectVehicle(vehicleId, request, userRole, complexId, selectedComplexId));
    }

    //전체 차량 조회
    @GetMapping("/api/admin/vehicles")
    public ResultResponse<PageResponse<AdminVehicleListRes>> getAdminVehicleList(
            @ModelAttribute AdminVehicleListReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("전체 차량 목록 조회 성공", vehicleService.getAdminVehicleList(request, userRole, complexId, selectedComplexId));
    }

    //차량 상태별 통계 조회
    @GetMapping("/api/admin/vehicle-stats")
    public ResultResponse<AdminVehicleStatsRes> getAdminVehicleStats(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("차량 상태별 통계 조회 성공", vehicleService.getAdminVehicleStats(userRole, complexId, selectedComplexId));
    }

    //차량 동/호 옵션 조회
    @GetMapping("/api/admin/vehicle-locations")
    public ResultResponse<AdminVehicleLocationRes> getAdminVehicleLocations(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("차량 동/호 옵션 조회 성공", vehicleService.getAdminVehicleLocations(userRole, complexId, selectedComplexId));
    }

    //관리자 차량 상세 조회
    @GetMapping("/api/admin/vehicles/{vehicleId}")
    public ResultResponse<AdminVehicleDetailRes> getAdminVehicleDetail(
            @PathVariable Long vehicleId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("관리자 차량 상세 조회 성공", vehicleService.getAdminVehicleDetail(vehicleId, userRole, complexId, selectedComplexId));
    }
}
