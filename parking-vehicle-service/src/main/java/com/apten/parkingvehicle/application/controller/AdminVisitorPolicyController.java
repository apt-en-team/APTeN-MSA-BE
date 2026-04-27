package com.apten.parkingvehicle.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.parkingvehicle.application.model.request.VisitorPolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyGetRes;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyPutRes;
import com.apten.parkingvehicle.application.service.ParkingPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 방문차량 정책 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/visitor-policies")
public class AdminVisitorPolicyController {

    // 방문차량 정책 응용 서비스이다.
    private final ParkingPolicyService parkingPolicyService;

    //방문차량 정책 설정 API-320
    @PutMapping
    public ResultResponse<VisitorPolicyPutRes> updateVisitorPolicy(
            @RequestParam Long complexId,
            @RequestBody VisitorPolicyPutReq request
    ) {
        return ResultResponse.success("방문차량 정책 설정 성공", parkingPolicyService.updateVisitorPolicy(complexId, request));
    }

    //방문차량 정책 조회 API-321
    @GetMapping
    public ResultResponse<VisitorPolicyGetRes> getVisitorPolicy(@RequestParam Long complexId) {
        return ResultResponse.success("방문차량 정책 조회 성공", parkingPolicyService.getVisitorPolicy(complexId));
    }
}
