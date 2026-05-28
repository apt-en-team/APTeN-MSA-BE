package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxWaitingRes;
import com.apten.facilityreservation.application.model.response.MyGxReservationListRes;
import java.util.List;
import com.apten.facilityreservation.application.service.GxReservationService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// 입주민 GX 예약 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gx-reservations")
public class GxReservationController {

    private final GxReservationService gxReservationService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // API-640 내 GX 예약 목록 조회
    @GetMapping("/my")
    public ResultResponse<List<MyGxReservationListRes>> getMyGxReservations(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("내 GX 예약 목록 조회 성공", gxReservationService.getMyGxReservations(context.getUserId(), context.getComplexId()));
    }

    // API-637 GX 예약 신청
    @PostMapping
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
    @GetMapping("/{gxReservationId}/waiting")
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
    @PatchMapping("/{gxReservationId}/cancel")
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
