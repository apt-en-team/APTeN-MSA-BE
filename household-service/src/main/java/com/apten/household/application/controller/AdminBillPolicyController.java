package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.BillPolicyPutReq;
import com.apten.household.application.model.response.BillPolicyPutRes;
import com.apten.household.application.service.BillPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 청구 기본 정책 설정 API 진입점이다.
// 현재 단계에서는 관리자 인증 컨텍스트 대신 request parameter로 complexId를 받는다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/households/policies")
public class AdminBillPolicyController {

    // 청구 기본 정책 원본 저장을 담당하는 응용 서비스이다.
    private final BillPolicyService billPolicyService;

    // 청구 기본 정책 설정 API이다.
    @PutMapping("/basic")
    public ResultResponse<BillPolicyPutRes> updateBillPolicy(
            @RequestParam Long complexId,
            @RequestBody BillPolicyPutReq req
    ) {
        return ResultResponse.success(
                "청구 기본 정책 설정 성공",
                billPolicyService.updateBillPolicy(complexId, req)
        );
    }
}
