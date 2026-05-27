package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.VehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VehicleListReq;
import com.apten.parkingvehicle.application.model.response.LicensePlateCheckRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.VehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.VehiclePatchRes;
import com.apten.parkingvehicle.application.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 입주민 차량 API 진입점
@RestController
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    //차량 등록 신청
    @PostMapping("/api/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VehicleCreateRes> createVehicle(
            @RequestBody VehicleCreateReq request,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("차량 등록 신청 성공", vehicleService.createVehicle(request, userId, userRole, complexId));
    }

    //차량 수정
    @PatchMapping("/api/vehicles/{vehicleId}")
    public ResultResponse<VehiclePatchRes> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody VehiclePatchReq request,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("차량 수정 성공", vehicleService.updateVehicle(vehicleId, request, userId, userRole, complexId));
    }

    //차량 삭제
    @DeleteMapping("/api/vehicles/{vehicleId}")
    public ResultResponse<VehicleDeleteRes> deleteVehicle(
            @PathVariable Long vehicleId,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("차량 삭제 성공", vehicleService.deleteVehicle(vehicleId, userId, userRole, complexId));
    }

    //내 차량 목록 조회
    @GetMapping("/api/vehicles")
    public ResultResponse<PageResponse<com.apten.parkingvehicle.application.model.response.VehicleListRes>> getMyVehicleList(
            @ModelAttribute VehicleListReq request,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("내 차량 목록 조회 성공", vehicleService.getMyVehicleList(request, userId, userRole, complexId));
    }

    //내 차량 상세 조회
    @GetMapping("/api/vehicles/{vehicleId}")
    public ResultResponse<VehicleDetailRes> getMyVehicleDetail(
            @PathVariable Long vehicleId,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("내 차량 상세 조회 성공", vehicleService.getMyVehicleDetail(vehicleId, userId, userRole, complexId));
    }

    //차량번호 중복 확인
    @GetMapping("/api/vehicles/check-license-plate")
    public ResultResponse<LicensePlateCheckRes> checkDuplicateLicensePlate(
            @RequestParam String licensePlate,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("차량번호 중복 확인 성공", vehicleService.checkDuplicateLicensePlate(licensePlate, userId, userRole, complexId));
    }
}
