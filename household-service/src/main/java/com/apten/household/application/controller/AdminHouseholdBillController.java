package com.apten.household.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.AdminHouseholdBillListReq;
import com.apten.household.application.model.request.BaseFeeReflectReq;
import com.apten.household.application.model.response.AdminHouseholdBillDetailRes;
import com.apten.household.application.model.response.AdminHouseholdBillListRes;
import com.apten.household.application.model.response.BaseFeeReflectRes;
import com.apten.household.application.model.response.BillConfirmRes;
import com.apten.household.application.model.response.BillUnconfirmRes;
import com.apten.household.application.service.HouseholdRequestContextResolver;
import com.apten.household.application.service.HouseholdBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 세대 비용 도메인 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/household-bills")
public class AdminHouseholdBillController {

    // 세대 비용 도메인 응용 서비스
    private final HouseholdBillService householdBillService;

    // 관리자 헤더 기준 단지 컨텍스트를 해석한다.
    private final HouseholdRequestContextResolver householdRequestContextResolver;

    @PostMapping("/base-fees")
    public ResultResponse<BaseFeeReflectRes> reflectBaseFee(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody BaseFeeReflectReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("기본 관리비 반영 성공", householdBillService.reflectBaseFee(context.getComplexId(), request));
    }

    //관리자 관리비 목록 조회 API-423
    @GetMapping
    public ResultResponse<AdminHouseholdBillListRes> getAdminBills(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute AdminHouseholdBillListReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 관리비 목록 조회 성공", householdBillService.getAdminBills(context.getComplexId(), request));
    }

    //관리자 관리비 상세 조회 API-424
    @GetMapping("/{billId}")
    public ResultResponse<AdminHouseholdBillDetailRes> getAdminBillDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long billId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 관리비 상세 조회 성공", householdBillService.getAdminBillDetail(context.getComplexId(), billId));
    }

    //월별 비용 확정 API-420
    @PatchMapping("/{billId}/confirm")
    public ResultResponse<BillConfirmRes> confirmHouseholdBill(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long billId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 비용 확정 성공", householdBillService.confirmBill(context.getComplexId(), billId));
    }

    // 월별 비용 확정 취소 API-FR426
    @PatchMapping("/{billId}/unconfirm")
    public ResultResponse<BillUnconfirmRes> unconfirmHouseholdBill(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long billId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 비용 확정 취소 성공", householdBillService.unconfirmBill(context.getComplexId(), billId));
    }
}
