package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.request.ComplexPolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.FacilityPolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.VehiclePolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.VisitorPolicyPutReq;
import com.apten.apartmentcomplex.application.model.response.ComplexPolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.FacilityPolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.VehiclePolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.VisitorPolicyPutRes;
import com.apten.apartmentcomplex.application.service.ComplexPolicyService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 정책 API 진입점
// 단지 기본 정책과 차량, 시설, 방문차량 정책을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/apartment-complexes/{code}/policies")
public class AdminComplexPolicyController {

    // 정책 응용 서비스
    private final ComplexPolicyService complexPolicyService;

    // 기본 정책 설정 API-206
    @PutMapping("/basic")
    public ResultResponse<ComplexPolicyPutRes> updateBasicPolicy(
            @PathVariable String code,
            @RequestBody ComplexPolicyPutReq req
    ) {
        return ResultResponse.success(
                "기본 정책 설정 성공",
                complexPolicyService.updateBasicPolicy(code, req)
        );
    }

    // 차량 정책 설정 API-207
    @PutMapping("/vehicle")
    public ResultResponse<VehiclePolicyPutRes> updateVehiclePolicy(
            @PathVariable String code,
            @RequestBody VehiclePolicyPutReq req
    ) {
        return ResultResponse.success(
                "차량 정책 설정 성공",
                complexPolicyService.updateVehiclePolicy(code, req)
        );
    }

    // 시설 정책 설정 API-208
    @PutMapping("/facility")
    public ResultResponse<FacilityPolicyPutRes> updateFacilityPolicy(
            @PathVariable String code,
            @RequestBody FacilityPolicyPutReq req
    ) {
        return ResultResponse.success(
                "시설 정책 설정 성공",
                complexPolicyService.updateFacilityPolicy(code, req)
        );
    }

    // 방문차량 정책 설정 API-209
    @PutMapping("/visitor-vehicle")
    public ResultResponse<VisitorPolicyPutRes> updateVisitorPolicy(
            @PathVariable String code,
            @RequestBody VisitorPolicyPutReq req
    ) {
        return ResultResponse.success(
                "방문차량 정책 설정 성공",
                complexPolicyService.updateVisitorPolicy(code, req)
        );
    }
}
