package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.AdminReservationCancelReq;
import com.apten.facilityreservation.application.model.request.AdminReservationListReq;
import com.apten.facilityreservation.application.model.response.AdminReservationCancelRes;
import com.apten.facilityreservation.application.model.response.AdminReservationDetailRes;
import com.apten.facilityreservation.application.model.response.AdminReservationListRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 관리자 예약 관리 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    //관리자 예약 목록 조회 API-626
    @GetMapping("/api/admin/reservations")
    public ResultResponse<PageResponse<AdminReservationListRes>> getAdminReservationList(@ModelAttribute AdminReservationListReq req) {
        return ResultResponse.success("관리자 예약 목록 조회 성공", reservationService.getAdminReservationList(req));
    }

    //관리자 예약 상세 조회 API-627
    @GetMapping("/api/admin/reservations/{reservationId}")
    public ResultResponse<AdminReservationDetailRes> getAdminReservationDetail(@PathVariable Long reservationId) {
        return ResultResponse.success("관리자 예약 상세 조회 성공", reservationService.getAdminReservationDetail(reservationId));
    }

    //관리자 예약 강제 취소 API-628
    @PatchMapping("/api/admin/reservations/{reservationId}/cancel")
    public ResultResponse<AdminReservationCancelRes> cancelReservationByAdmin(
            @PathVariable Long reservationId,
            @RequestBody AdminReservationCancelReq req
    ) {
        return ResultResponse.success("관리자 예약 강제 취소 성공", reservationService.cancelReservationByAdmin(reservationId, req));
    }
}
