package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.AdminGxReservationListReq;
import com.apten.facilityreservation.application.model.request.GxBulkApproveReq;
import com.apten.facilityreservation.application.model.request.GxCloseWaitingReq;
import com.apten.facilityreservation.application.model.request.GxProgramCancelReq;
import com.apten.facilityreservation.application.model.request.GxProgramListReq;
import com.apten.facilityreservation.application.model.request.GxProgramPatchReq;
import com.apten.facilityreservation.application.model.request.GxProgramPostReq;
import com.apten.facilityreservation.application.model.response.AdminGxReservationListRes;
import com.apten.facilityreservation.application.model.response.GxBulkApproveRes;
import com.apten.facilityreservation.application.model.response.GxCloseWaitingRes;
import com.apten.facilityreservation.application.model.response.GxMinimumCheckRes;
import com.apten.facilityreservation.application.model.response.GxProgramCancelRes;
import com.apten.facilityreservation.application.model.response.GxProgramDetailRes;
import com.apten.facilityreservation.application.model.response.GxProgramListRes;
import com.apten.facilityreservation.application.model.response.GxProgramPatchRes;
import com.apten.facilityreservation.application.model.response.GxProgramPostRes;
import com.apten.facilityreservation.application.model.response.GxStatusRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.service.GxProgramService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// 관리자 GX 프로그램 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/gx-programs")
public class AdminGxProgramController {

    private final GxProgramService gxProgramService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // API-630 GX 프로그램 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<GxProgramPostRes> createGxProgram(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody GxProgramPostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 프로그램 등록 성공", gxProgramService.createGxProgram(context.getComplexId(), req));
    }

    // API-631 GX 프로그램 목록 조회
    @GetMapping
    public ResultResponse<PageResponse<GxProgramListRes>> getAdminGxProgramList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute GxProgramListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 프로그램 목록 조회 성공", gxProgramService.getAdminGxProgramList(context.getComplexId(), req));
    }

    // API-632 GX 프로그램 상세 조회
    @GetMapping("/{programId}")
    public ResultResponse<GxProgramDetailRes> getAdminGxProgramDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long programId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 프로그램 상세 조회 성공", gxProgramService.getAdminGxProgramDetail(context.getComplexId(), programId));
    }

    // API-633 GX 프로그램 수정
    @PatchMapping("/{programId}")
    public ResultResponse<GxProgramPatchRes> updateGxProgram(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long programId,
            @RequestBody GxProgramPatchReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 프로그램 수정 성공", gxProgramService.updateGxProgram(context.getComplexId(), programId, req));
    }

    // API-634 GX 프로그램 취소
    @PatchMapping("/{programId}/cancel")
    public ResultResponse<GxProgramCancelRes> cancelGxProgram(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long programId,
            @RequestBody GxProgramCancelReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 프로그램 취소 성공", gxProgramService.cancelGxProgram(context.getComplexId(), programId, req));
    }

    // API-648 GX 모집 마감
    @PatchMapping("/{programId}/close-waiting")
    public ResultResponse<GxCloseWaitingRes> closeWaiting(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long programId,
            @RequestBody(required = false) GxCloseWaitingReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 모집 마감 처리 성공", gxProgramService.closeWaiting(context.getComplexId(), programId, req));
    }

    // GX 프로그램 신청자 목록 조회
    @GetMapping("/{programId}/reservations")
    public ResultResponse<PageResponse<AdminGxReservationListRes>> getAdminGxReservationList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long programId,
            @ModelAttribute AdminGxReservationListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 프로그램 신청자 목록 조회 성공", gxProgramService.getAdminGxReservationList(context.getComplexId(), programId, req));
    }

    // API-642 GX 일괄 승인 처리
    @PostMapping("/{programId}/bulk-approve")
    public ResultResponse<GxBulkApproveRes> bulkApprove(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long programId,
            @RequestBody GxBulkApproveReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 일괄 승인 처리 성공", gxProgramService.bulkApprove(context.getComplexId(), programId, req));
    }

    // API-643 GX 최소 인원 검증
    @PostMapping("/{programId}/minimum-check")
    public ResultResponse<GxMinimumCheckRes> checkMinimum(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long programId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 최소 인원 검증 성공", gxProgramService.checkMinimum(context.getComplexId(), programId));
    }

    // API-647 GX 현황 조회
    @GetMapping("/{programId}/status")
    public ResultResponse<GxStatusRes> getGxStatus(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long programId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("GX 현황 조회 성공", gxProgramService.getGxStatus(context.getComplexId(), programId));
    }
}
