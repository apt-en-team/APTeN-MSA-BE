package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxWaitingRes;
import com.apten.facilityreservation.application.service.GxReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 입주민 GX 예약 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class GxReservationController {

    private final GxReservationService gxReservationService;

    //GX 예약 신청 API-637
    @PostMapping("/api/gx-reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<GxReservationPostRes> createGxReservation(@RequestBody GxReservationPostReq req) {
        return ResultResponse.success("GX 예약 신청 성공", gxReservationService.createGxReservation(req));
    }

    //GX 대기 순번 조회 API-638
    @GetMapping("/api/gx-reservations/{gxReservationId}/waiting")
    public ResultResponse<GxWaitingRes> getWaiting(@PathVariable Long gxReservationId) {
        return ResultResponse.success("GX 대기 순번 조회 성공", gxReservationService.getWaiting(gxReservationId));
    }

    //GX 예약 취소 API-639
    @PatchMapping("/api/gx-reservations/{gxReservationId}/cancel")
    public ResultResponse<GxReservationCancelRes> cancelGxReservation(@PathVariable Long gxReservationId) {
        return ResultResponse.success("GX 예약 취소 성공", gxReservationService.cancelGxReservation(gxReservationId));
    }
}
