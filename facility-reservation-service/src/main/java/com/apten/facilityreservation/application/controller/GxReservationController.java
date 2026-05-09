package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxWaitingRes;
import com.apten.facilityreservation.application.service.GxReservationService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

// 입주민 GX 예약 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class GxReservationController {

    private final GxReservationService gxReservationService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // API-637 GX 예약 신청
    @PostMapping("/api/gx-reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<GxReservationPostRes> createGxReservation(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @RequestBody GxReservationPostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("GX 예약 신청 성공", gxReservationService.createGxReservation(context.getUserId(), context.getComplexId(), req));
    }

    // API-638 GX 대기 순번 조회
    @GetMapping("/api/gx-reservations/{gxReservationId}/waiting")
    public ResultResponse<GxWaitingRes> getWaiting(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long gxReservationId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("GX 대기 순번 조회 성공", gxReservationService.getWaiting(context.getUserId(), context.getComplexId(), gxReservationId));
    }

    // API-639 GX 예약 취소
    @PatchMapping("/api/gx-reservations/{gxReservationId}/cancel")
    public ResultResponse<GxReservationCancelRes> cancelGxReservation(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long gxReservationId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("GX 예약 취소 성공", gxReservationService.cancelGxReservation(context.getUserId(), context.getComplexId(), gxReservationId));
    }
}
