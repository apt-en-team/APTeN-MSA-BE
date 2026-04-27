package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.ComplexPolicyPutReq;
import com.apten.household.application.model.response.ComplexPolicyPutRes;
import com.apten.household.application.service.ComplexPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 청구 기본 정책 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/household-bill-policies")
public class AdminBillPolicyController {

    // 청구 기본 정책 원본 저장을 담당하는 응용 서비스이다.
    private final ComplexPolicyService complexPolicyService;

    //기본 관리비 정책 설정 API-416
    @PutMapping("/basic")
    public ResultResponse<ComplexPolicyPutRes> updateBillPolicy(
            @RequestParam Long complexId,
            @RequestBody ComplexPolicyPutReq req
    ) {
        return ResultResponse.success(
                "청구 기본 정책 설정 성공",
                complexPolicyService.updateComplexPolicy(complexId, req)
        );
    }
}
