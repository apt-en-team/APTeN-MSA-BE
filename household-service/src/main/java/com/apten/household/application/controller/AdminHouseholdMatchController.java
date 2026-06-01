package com.apten.household.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.HouseholdMatchBulkApproveReq;
import com.apten.household.application.model.request.HouseholdMatchListReq;
import com.apten.household.application.model.request.HouseholdMatchRejectReq;
import com.apten.household.application.model.response.HouseholdMatchApproveRes;
import com.apten.household.application.model.response.HouseholdMatchListRes;
import com.apten.household.application.model.response.HouseholdMatchRejectRes;
import com.apten.household.application.service.HouseholdRequestContextResolver;
import com.apten.household.application.service.HouseholdMatchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 세대 매칭 도메인 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/household-match-requests")
public class AdminHouseholdMatchController {

    // 세대 매칭 도메인 응용 서비스
    private final HouseholdMatchService householdMatchService;

    // 관리자 헤더 기준 단지 컨텍스트를 해석한다.
    private final HouseholdRequestContextResolver householdRequestContextResolver;

    //수동 승인 대상 조회 API-413
    @GetMapping
    public ResultResponse<HouseholdMatchListRes> getHouseholdMatchRequestList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute HouseholdMatchListReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 매칭 요청 목록 조회 성공", householdMatchService.getMatchRequestList(context.getComplexId(), request));
    }

    //수동 승인 처리 API-414
    @PatchMapping("/approve-bulk")
    public ResultResponse<List<HouseholdMatchApproveRes>> approveMatchRequests(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody HouseholdMatchBulkApproveReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 매칭 일괄 승인 성공", householdMatchService.approveMatchRequests(context.getComplexId(), request));
    }

    @PatchMapping("/{matchRequestId}/approve")
    public ResultResponse<HouseholdMatchApproveRes> approveMatchRequest(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long matchRequestId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 매칭 승인 성공", householdMatchService.approveMatchRequest(context.getComplexId(), matchRequestId));
    }

    //수동 거절 처리 API-415
    @PatchMapping("/{matchRequestId}/reject")
    public ResultResponse<HouseholdMatchRejectRes> rejectMatchRequest(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long matchRequestId,
            @RequestBody HouseholdMatchRejectReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 매칭 거절 성공", householdMatchService.rejectMatchRequest(context.getComplexId(), matchRequestId, request));
    }
}
