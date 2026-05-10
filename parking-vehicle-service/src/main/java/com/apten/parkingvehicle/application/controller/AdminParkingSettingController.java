package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.ParkingSettingPatchReq;
import com.apten.parkingvehicle.application.model.response.ParkingSettingGetRes;
import com.apten.parkingvehicle.application.model.response.ParkingSettingPatchRes;
import com.apten.parkingvehicle.application.service.ParkingSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// 관리자 주차 운영 타입 설정 API 진입점
@RestController
@RequiredArgsConstructor
public class AdminParkingSettingController {

    private final ParkingSettingService parkingSettingService;

    // 단지의 주차 운영 타입을 조회한다.
    @GetMapping("/api/admin/parking/setting")
    public ResultResponse<ParkingSettingGetRes> getParkingSetting(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 운영 타입 조회 성공",
                parkingSettingService.getParkingSetting(userRole, complexId, selectedComplexId)
        );
    }

    // 단지의 주차 운영 타입을 변경한다.
    @PatchMapping("/api/admin/parking/setting")
    public ResultResponse<ParkingSettingPatchRes> updateParkingSetting(
            @RequestBody ParkingSettingPatchReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "주차 운영 타입 변경 성공",
                parkingSettingService.updateParkingSetting(request, userRole, complexId, selectedComplexId)
        );
    }
}