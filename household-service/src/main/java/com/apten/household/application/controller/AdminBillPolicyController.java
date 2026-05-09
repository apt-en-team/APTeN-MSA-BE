package com.apten.household.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.ComplexPolicyPutReq;
import com.apten.household.application.model.response.ComplexPolicyPutRes;
import com.apten.household.application.service.ComplexPolicyService;
import com.apten.household.application.service.HouseholdRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 청구 기본 정책 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/household-bill-policies")
public class AdminBillPolicyController {

    // 청구 기본 정책 원본 저장을 담당하는 응용 서비스이다.
    private final ComplexPolicyService complexPolicyService;

    // 관리자 헤더 기준 단지 컨텍스트를 해석한다.
    private final HouseholdRequestContextResolver householdRequestContextResolver;

    //기본 관리비 정책 설정 API-416
    @PutMapping("/basic")
    public ResultResponse<ComplexPolicyPutRes> updateBillPolicy(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody ComplexPolicyPutReq req
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success(
                "청구 기본 정책 설정 성공",
                complexPolicyService.updateComplexPolicy(context.getComplexId(), req)
        );
    }
}
