package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.GxReservationRejectReq;
import com.apten.facilityreservation.application.model.response.GxReservationApproveRes;
import com.apten.facilityreservation.application.model.response.GxReservationRejectRes;
import com.apten.facilityreservation.application.service.GxReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 관리자 GX 예약 승인 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class AdminGxReservationController {

    private final GxReservationService gxReservationService;

    //GX 단건 승인 처리 API-640
    @PatchMapping("/api/admin/gx-reservations/{gxReservationId}/approve")
    public ResultResponse<GxReservationApproveRes> approveGxReservation(@PathVariable Long gxReservationId) {
        return ResultResponse.success("GX 단건 승인 처리 성공", gxReservationService.approveGxReservation(gxReservationId));
    }

    //GX 단건 거절 처리 API-641
    @PatchMapping("/api/admin/gx-reservations/{gxReservationId}/reject")
    public ResultResponse<GxReservationRejectRes> rejectGxReservation(
            @PathVariable Long gxReservationId,
            @RequestBody GxReservationRejectReq req
    ) {
        return ResultResponse.success("GX 단건 거절 처리 성공", gxReservationService.rejectGxReservation(gxReservationId, req));
    }
}
