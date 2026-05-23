package com.apten.household.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.ExpectedResidentCreateReq;
import com.apten.household.application.model.request.ExpectedResidentListReq;
import com.apten.household.application.model.response.ExpectedResidentListRes;
import com.apten.household.application.model.response.ExpectedResidentRes;
import com.apten.household.application.service.ExpectedResidentService;
import com.apten.household.application.service.HouseholdRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 입주민 명부 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/expected-residents")
public class AdminExpectedResidentController {

    // 관리자 입주민 명부 적용 서비스이다.
    private final ExpectedResidentService expectedResidentService;

    // 관리자 헤더 기준 단지 컨텍스트를 해석한다.
    private final HouseholdRequestContextResolver householdRequestContextResolver;

    // 관리자 입주민 명부를 등록한다.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ExpectedResidentRes> registerExpectedResident(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody ExpectedResidentCreateReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("입주민 명부 등록 성공", expectedResidentService.registerExpectedResident(context, request));
    }

    // 관리자 입주민 명부 목록을 조회한다.
    @GetMapping
    public ResultResponse<ExpectedResidentListRes> getExpectedResidentList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute ExpectedResidentListReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("입주민 명부 목록 조회 성공", expectedResidentService.getExpectedResidentList(context, request));
    }
}
