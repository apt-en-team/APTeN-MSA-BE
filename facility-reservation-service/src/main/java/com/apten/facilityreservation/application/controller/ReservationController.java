package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.AvailableTimeListReq;
import com.apten.facilityreservation.application.model.request.MyReservationListReq;
import com.apten.facilityreservation.application.model.request.MyUnifiedReservationReq;
import com.apten.facilityreservation.application.model.response.MyUnifiedReservationRes;
import com.apten.facilityreservation.application.model.request.ReservationCancelReq;
import com.apten.facilityreservation.application.model.request.ReservationPostReq;
import com.apten.facilityreservation.application.model.request.SeatHoldPostReq;
import com.apten.facilityreservation.application.model.response.AvailableTimeListRes;
import com.apten.facilityreservation.application.model.response.MyReservationDetailRes;
import com.apten.facilityreservation.application.model.response.MyReservationListRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.ReservationCancelRes;
import com.apten.facilityreservation.application.model.response.ReservationPostRes;
import com.apten.facilityreservation.application.model.response.SeatHoldPostRes;
import com.apten.facilityreservation.application.service.ReservationService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 일반 시설 예약 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // API-619 예약 가능 시간 조회
    @GetMapping("/api/reservations/available-times")
    public ResultResponse<List<AvailableTimeListRes>> getAvailableTimeList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @ModelAttribute AvailableTimeListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("예약 가능 시간 조회 성공", reservationService.getAvailableTimeList(context.getUserId(), context.getComplexId(), req));
    }

    // API-620 좌석 임시 선점
    @PostMapping("/api/reservations/seat-holds")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<SeatHoldPostRes> createSeatHold(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @RequestBody SeatHoldPostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("좌석 임시 선점 성공", reservationService.createSeatHold(context.getUserId(), context.getComplexId(), req));
    }

    // API-622 예약 생성
    @PostMapping("/api/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ReservationPostRes> createReservation(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @RequestBody ReservationPostReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("예약 생성 성공", reservationService.createReservation(context.getUserId(), context.getComplexId(), req));
    }

    // API-623 내 예약 통합 목록 조회 (일반 시설 + GX 통합, 페이지네이션)
    @GetMapping("/api/reservations/my")
    public ResultResponse<PageResponse<MyUnifiedReservationRes>> getMyUnifiedReservations(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @ModelAttribute MyUnifiedReservationReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("내 예약 목록 조회 성공", reservationService.getMyUnifiedReservations(context.getUserId(), context.getComplexId(), req));
    }

    // API-624 내 예약 상세 조회
    @GetMapping("/api/reservations/{reservationId}")
    public ResultResponse<MyReservationDetailRes> getMyReservationDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long reservationId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("내 예약 상세 조회 성공", reservationService.getMyReservationDetail(context.getUserId(), context.getComplexId(), reservationId));
    }

    // API-625 예약 취소
    @PatchMapping("/api/reservations/{reservationId}/cancel")
    public ResultResponse<ReservationCancelRes> cancelReservation(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long reservationId,
            @RequestBody(required = false) ReservationCancelReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success(
                "예약 취소 성공",
                reservationService.cancelReservation(context.getUserId(), context.getComplexId(), reservationId, req == null ? ReservationCancelReq.builder().build() : req)
        );
    }
}
