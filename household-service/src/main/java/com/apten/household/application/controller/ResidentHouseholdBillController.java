package com.apten.household.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.MyBillListReq;
import com.apten.household.application.model.response.AdminHouseholdBillDetailRes;
import com.apten.household.application.model.response.MyBillListRes;
import com.apten.household.application.service.HouseholdBillService;
import com.apten.household.application.service.HouseholdRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 입주민 세대 비용 조회 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/household-bills")
public class ResidentHouseholdBillController {

    // 세대 비용 도메인 응용 서비스
    private final HouseholdBillService householdBillService;

    // Header 기반 요청 컨텍스트 해석기
    private final HouseholdRequestContextResolver householdRequestContextResolver;

    //세대 비용 조회 API-421
    @GetMapping
    public ResultResponse<MyBillListRes> getMyBills(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @ModelAttribute MyBillListReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success(
                "세대 비용 조회 성공",
                householdBillService.getMyBills(context.getUserId(), context.getComplexId(), request)
        );
    }

    @GetMapping("/{billId}")
    public ResultResponse<AdminHouseholdBillDetailRes> getMyBillDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long billId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success(
                "내 관리비 상세 조회 성공",
                householdBillService.getMyBillDetail(context.getUserId(), context.getComplexId(), billId)
        );
    }
}
