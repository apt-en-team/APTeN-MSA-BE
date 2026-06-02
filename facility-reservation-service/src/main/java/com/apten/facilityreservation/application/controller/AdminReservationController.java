package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.AdminReservationCancelReq;
import com.apten.facilityreservation.application.model.request.AdminReservationListReq;
import com.apten.facilityreservation.application.model.request.AdminReservationOverviewReq;
import com.apten.facilityreservation.application.model.response.AdminReservationCancelRes;
import com.apten.facilityreservation.application.model.response.AdminReservationDetailRes;
import com.apten.facilityreservation.application.model.response.AdminReservationListRes;
import com.apten.facilityreservation.application.model.response.AdminReservationOverviewRes;
import com.apten.facilityreservation.application.model.response.AdminReservationStatsRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.service.AdminReservationOverviewService;
import com.apten.facilityreservation.application.service.AdminReservationStatsService;
import com.apten.facilityreservation.application.service.ReservationService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 관리자 예약 관리 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;
    private final AdminReservationOverviewService adminReservationOverviewService;
    private final AdminReservationStatsService adminReservationStatsService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // 관리자 예약 목록 조회
    @GetMapping
    public ResultResponse<PageResponse<AdminReservationListRes>> getAdminReservationList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute AdminReservationListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 예약 목록 조회 성공", reservationService.getAdminReservationList(context.getComplexId(), req));
    }

    // 관리자 예약 통계 조회 (literal 경로 우선)
    @GetMapping("/stats")
    public ResultResponse<AdminReservationStatsRes> getAdminReservationStats(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 예약 통계 조회 성공", adminReservationStatsService.getStats(context.getComplexId()));
    }

    // 관리자 예약 통합 개요 조회 (FACILITY/GX, literal 경로 우선)
    @GetMapping("/overview")
    public ResultResponse<PageResponse<AdminReservationOverviewRes>> getAdminReservationOverview(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute AdminReservationOverviewReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 예약 통합 개요 조회 성공", adminReservationOverviewService.getOverview(context.getComplexId(), req));
    }

    // 관리자 예약 상세 조회
    @GetMapping("/{reservationId}")
    public ResultResponse<AdminReservationDetailRes> getAdminReservationDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long reservationId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 예약 상세 조회 성공", reservationService.getAdminReservationDetail(context.getComplexId(), reservationId));
    }

    // 관리자 예약 강제 취소
    @PatchMapping("/{reservationId}/cancel")
    public ResultResponse<AdminReservationCancelRes> cancelReservationByAdmin(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long reservationId,
            @RequestBody(required = false) AdminReservationCancelReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("관리자 예약 강제 취소 성공", reservationService.cancelReservationByAdmin(context.getComplexId(), reservationId, req));
    }
}
