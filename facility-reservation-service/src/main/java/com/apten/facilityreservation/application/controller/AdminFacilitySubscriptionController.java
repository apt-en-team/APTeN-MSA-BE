package com.apten.facilityreservation.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.response.AdminFacilitySubscriptionListRes;
import com.apten.facilityreservation.application.model.response.AdminHouseholdSubscriptionDetailRes;
import com.apten.facilityreservation.application.model.response.AdminHouseholdSubscriptionSummaryRes;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import com.apten.facilityreservation.application.service.FacilitySubscriptionService;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 관리자 시설 구독 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/facility-subscriptions")
public class AdminFacilitySubscriptionController {

    private final FacilitySubscriptionService facilitySubscriptionService;
    private final FacilityRequestContextResolver contextResolver;

    // 관리자 구독 목록 조회
    // 관리자 단지 컨텍스트 해석 (MASTER 선택 단지, ADMIN/MANAGER 소속 단지)
    @GetMapping
    public ResultResponse<List<AdminFacilitySubscriptionListRes>> getAdminSubscriptionList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) FacilitySubscriptionStatus status
    ) {
        FacilityRequestContext context = contextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("구독 목록 조회 성공",
                facilitySubscriptionService.getAdminSubscriptionList(context.getComplexId(), facilityId, status));
    }

    // 관리자 세대별 구독 요약 목록 조회
    @GetMapping("/households")
    public ResultResponse<List<AdminHouseholdSubscriptionSummaryRes>> getHouseholdSubscriptionList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        FacilityRequestContext context = contextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대별 구독 목록 조회 성공",
                facilitySubscriptionService.getHouseholdSubscriptionList(context.getComplexId()));
    }

    // 관리자 세대별 구독 상세 조회
    @GetMapping("/households/{householdId}")
    public ResultResponse<AdminHouseholdSubscriptionDetailRes> getHouseholdSubscriptionDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId
    ) {
        FacilityRequestContext context = contextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대별 구독 상세 조회 성공",
                facilitySubscriptionService.getHouseholdSubscriptionDetail(context.getComplexId(), householdId));
    }

    // 관리자 구독 강제 해지
    @DeleteMapping("/{subscriptionId}")
    public ResultResponse<Void> adminCancelSubscription(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long subscriptionId
    ) {
        FacilityRequestContext context = contextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        facilitySubscriptionService.adminCancelSubscription(context.getComplexId(), subscriptionId);
        return ResultResponse.success("구독이 해지되었습니다.", null);
    }
}
