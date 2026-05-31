package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.AdminRegularVisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.AdminRegularVisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.response.AdminRegularVisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.AdminRegularVisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.AdminRegularVisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.service.RegularVisitorVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 고정 방문차량 삭제 API 진입점
@RestController
@RequiredArgsConstructor
public class AdminRegularVisitorVehicleController {

    private final RegularVisitorVehicleService regularVisitorVehicleService;

    //관리자 고정 방문차량 목록 조회
    @GetMapping("/api/admin/regular-visitor-vehicles")
    public ResultResponse<PageResponse<AdminRegularVisitorVehicleListRes>> getAdminRegularVisitorVehicleList(
            @ModelAttribute AdminRegularVisitorVehicleListReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "관리자 고정 방문차량 목록 조회 성공",
                regularVisitorVehicleService.getAdminRegularVisitorVehicleList(request, userRole, complexId, selectedComplexId)
        );
    }

    //관리자 고정 방문차량 등록
    @PostMapping("/api/admin/regular-visitor-vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AdminRegularVisitorVehicleCreateRes> createAdminRegularVisitorVehicle(
            @RequestBody AdminRegularVisitorVehicleCreateReq request,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "관리자 고정 방문차량 등록 성공",
                regularVisitorVehicleService.createAdminRegularVisitorVehicle(request, userId, userRole, complexId, selectedComplexId)
        );
    }

    //고정 방문차량 강제 삭제
    @DeleteMapping("/api/admin/regular-visitor-vehicles/{regularVisitorVehicleId}")
    public ResultResponse<AdminRegularVisitorVehicleDeleteRes> deleteRegularVisitorVehicleByAdmin(
            @PathVariable Long regularVisitorVehicleId,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "고정 방문차량 강제 삭제 성공",
                regularVisitorVehicleService.deleteRegularVisitorVehicleByAdmin(
                        regularVisitorVehicleId, userId, userRole, complexId, selectedComplexId)
        );
    }
}
