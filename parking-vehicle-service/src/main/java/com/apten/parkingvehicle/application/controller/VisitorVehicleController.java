package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleCreateReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleListReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehiclePatchReq;
import com.apten.parkingvehicle.application.model.request.VisitorVehicleReRegisterReq;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCancelRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleCreateRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDeleteRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleDetailRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleListRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehiclePatchRes;
import com.apten.parkingvehicle.application.model.response.VisitorVehicleReRegisterRes;
import com.apten.parkingvehicle.application.service.VisitorVehicleService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 방문차량 사용자 API 진입점
@RestController
@RequiredArgsConstructor
public class VisitorVehicleController {

    private final VisitorVehicleService visitorVehicleService;

    //방문차량 등록
    @PostMapping("/api/visitor-vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VisitorVehicleCreateRes> createVisitorVehicle(
            @RequestBody VisitorVehicleCreateReq request,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("방문차량 등록 성공", visitorVehicleService.createVisitorVehicle(request, userId, userRole, complexId));
    }

    //방문차량 목록 조회
    @GetMapping("/api/visitor-vehicles")
    public ResultResponse<PageResponse<VisitorVehicleListRes>> getMyVisitorVehicleList(
            @ModelAttribute VisitorVehicleListReq request,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("방문차량 목록 조회 성공", visitorVehicleService.getMyVisitorVehicleList(request, userId, userRole, complexId));
    }

    //방문차량 상세 조회
    @GetMapping("/api/visitor-vehicles/{visitorVehicleId}")
    public ResultResponse<VisitorVehicleDetailRes> getMyVisitorVehicleDetail(
            @PathVariable Long visitorVehicleId,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("방문차량 상세 조회 성공", visitorVehicleService.getMyVisitorVehicleDetail(visitorVehicleId, userId, userRole, complexId));
    }

    //방문차량 수정
    @PatchMapping("/api/visitor-vehicles/{visitorVehicleId}")
    public ResultResponse<VisitorVehiclePatchRes> updateVisitorVehicle(
            @PathVariable Long visitorVehicleId,
            @RequestBody VisitorVehiclePatchReq request,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("방문차량 수정 성공", visitorVehicleService.updateVisitorVehicle(visitorVehicleId, request, userId, userRole, complexId));
    }

    //방문차량 취소
    @PatchMapping("/api/visitor-vehicles/{visitorVehicleId}/cancel")
    public ResultResponse<VisitorVehicleCancelRes> cancelVisitorVehicle(
            @PathVariable Long visitorVehicleId,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("방문차량 취소 성공", visitorVehicleService.cancelVisitorVehicle(visitorVehicleId, userId, userRole, complexId));
    }

    //방문차량 삭제
    @DeleteMapping("/api/visitor-vehicles/{visitorVehicleId}")
    public ResultResponse<VisitorVehicleDeleteRes> deleteVisitorVehicle(
            @PathVariable Long visitorVehicleId,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("방문차량 삭제 성공", visitorVehicleService.deleteVisitorVehicle(visitorVehicleId, userId, userRole, complexId));
    }

    //방문차량 재등록
    @PostMapping("/api/visitor-vehicles/{visitorVehicleId}/re-register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<VisitorVehicleReRegisterRes> reRegisterVisitorVehicle(
            @PathVariable Long visitorVehicleId,
            @RequestBody VisitorVehicleReRegisterReq request,
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("방문차량 재등록 성공", visitorVehicleService.reRegisterVisitorVehicle(visitorVehicleId, request, userId, userRole, complexId));
    }
}
