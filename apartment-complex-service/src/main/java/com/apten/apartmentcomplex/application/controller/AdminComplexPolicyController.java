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
@RequestMapping("/api/admin/apartment-complexes/{apartmentComplexUid}/policies")
public class AdminComplexPolicyController {

    // 정책 응용 서비스
    private final ComplexPolicyService complexPolicyService;

    // 기본 정책 설정 API
    @PutMapping("/basic")
    public ResultResponse<ComplexPolicyPutRes> updateBasicPolicy(
            @PathVariable String apartmentComplexUid,
            @RequestBody ComplexPolicyPutReq request
    ) {
        return ResultResponse.success(
                "기본 정책 설정 성공",
                complexPolicyService.updateBasicPolicy(apartmentComplexUid, request)
        );
    }

    // 차량 정책 설정 API
    @PutMapping("/vehicle")
    public ResultResponse<VehiclePolicyPutRes> updateVehiclePolicy(
            @PathVariable String apartmentComplexUid,
            @RequestBody VehiclePolicyPutReq request
    ) {
        return ResultResponse.success(
                "차량 정책 설정 성공",
                complexPolicyService.updateVehiclePolicy(apartmentComplexUid, request)
        );
    }

    // 시설 정책 설정 API
    @PutMapping("/facility")
    public ResultResponse<FacilityPolicyPutRes> updateFacilityPolicy(
            @PathVariable String apartmentComplexUid,
            @RequestBody FacilityPolicyPutReq request
    ) {
        return ResultResponse.success(
                "시설 정책 설정 성공",
                complexPolicyService.updateFacilityPolicy(apartmentComplexUid, request)
        );
    }

    // 방문차량 정책 설정 API
    @PutMapping("/visitor-vehicle")
    public ResultResponse<VisitorPolicyPutRes> updateVisitorPolicy(
            @PathVariable String apartmentComplexUid,
            @RequestBody VisitorPolicyPutReq request
    ) {
        return ResultResponse.success(
                "방문차량 정책 설정 성공",
                complexPolicyService.updateVisitorPolicy(apartmentComplexUid, request)
        );
    }
}
