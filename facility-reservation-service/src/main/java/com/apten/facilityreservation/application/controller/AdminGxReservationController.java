package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.GxReservationRejectReq;
import com.apten.facilityreservation.application.model.response.AdminGxReservationDetailRes;
import com.apten.facilityreservation.application.model.response.GxReservationApproveRes;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationRejectRes;
import com.apten.facilityreservation.application.service.GxReservationService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// 관리자 GX 예약 승인 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class AdminGxReservationController {

    private final GxReservationService gxReservationService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // 관리자 GX 예약 단건 상세 조회
    @GetMapping("/api/admin/gx-reservations/{gxReservationId}")
    public ResultResponse<AdminGxReservationDetailRes> getAdminGxReservationDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long gxReservationId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 GX 예약 상세 조회 성공", gxReservationService.getAdminGxReservationDetail(context.getComplexId(), gxReservationId));
    }

    // API-640 GX 단건 승인 처리
    @PatchMapping("/api/admin/gx-reservations/{gxReservationId}/approve")
    public ResultResponse<GxReservationApproveRes> approveGxReservation(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long gxReservationId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 단건 승인 처리 성공", gxReservationService.approveGxReservation(context.getComplexId(), gxReservationId));
    }

    // API-641 GX 단건 거절 처리
    @PatchMapping("/api/admin/gx-reservations/{gxReservationId}/reject")
    public ResultResponse<GxReservationRejectRes> rejectGxReservation(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long gxReservationId,
            @RequestBody GxReservationRejectReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 단건 거절 처리 성공", gxReservationService.rejectGxReservation(context.getComplexId(), gxReservationId, req));
    }

    // 관리자 GX 예약 강제 취소
    @PatchMapping("/api/admin/gx-reservations/{gxReservationId}/cancel")
    public ResultResponse<GxReservationCancelRes> cancelGxReservationByAdmin(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long gxReservationId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 GX 예약 강제 취소 성공", gxReservationService.cancelGxReservationByAdmin(context.getComplexId(), gxReservationId));
    }
}
