package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.application.model.response.FacilityPolicyPutRes;
import com.apten.facilityreservation.application.service.FacilityPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 시설 정책 설정 API 진입점이다.
// 현재 단계에서는 관리자 인증 컨텍스트 대신 request parameter로 complexId를 받는다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/facility-reservation/policies")
public class AdminFacilityPolicyController {

    // 시설 정책 원본 저장을 담당하는 응용 서비스이다.
    private final FacilityPolicyService facilityPolicyService;

    // 시설 정책 설정 API이다.
    @PutMapping("/facility")
    public ResultResponse<FacilityPolicyPutRes> updateFacilityPolicy(
            @RequestParam Long complexId,
            @RequestBody FacilityPolicyPutReq req
    ) {
        return ResultResponse.success(
                "시설 정책 설정 성공",
                facilityPolicyService.updateFacilityPolicy(complexId, req)
        );
    }
}
