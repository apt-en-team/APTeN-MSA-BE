package com.apten.parkingvehicle.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VisitorPolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyGetRes;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyPutRes;
import com.apten.parkingvehicle.application.service.ParkingPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 방문차량 정책 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/visitor-policies")
public class AdminVisitorPolicyController {

    // 방문차량 정책 응용 서비스이다.
    private final ParkingPolicyService parkingPolicyService;

    // 방문차량 정책 설정
    @PutMapping
    public ResultResponse<VisitorPolicyPutRes> updateVisitorPolicy(
            @RequestBody VisitorPolicyPutReq request,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("방문차량 정책 설정 성공", parkingPolicyService.updateVisitorPolicy(request, userRole, complexId, selectedComplexId));
    }

    // 방문차량 정책 조회
    @GetMapping
    public ResultResponse<VisitorPolicyGetRes> getVisitorPolicy(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success("방문차량 정책 조회 성공", parkingPolicyService.getVisitorPolicy(userRole, complexId, selectedComplexId));
    }
}
