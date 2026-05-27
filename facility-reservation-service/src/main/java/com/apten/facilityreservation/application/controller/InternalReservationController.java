package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.response.ReservationCompleteRes;
import com.apten.facilityreservation.application.model.response.TempHoldExpireRes;
import com.apten.facilityreservation.application.service.GxReservationService;
import com.apten.facilityreservation.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 내부 스케줄러용 예약 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalReservationController {

    private final ReservationService reservationService;
    private final GxReservationService gxReservationService;

    //좌석 임시 선점 자동 해제 API-621
    @PostMapping("/internal/expire")
    public ResultResponse<TempHoldExpireRes> expireSeatHolds() {
        return ResultResponse.success("좌석 임시 선점 자동 해제 성공", reservationService.expireSeatHolds());
    }

    //예약 완료 처리 API-629
    @PostMapping("/internal/complete")
    public ResultResponse<ReservationCompleteRes> completeReservations() {
        return ResultResponse.success("예약 완료 처리 성공", reservationService.completeReservations());
    }

    //GX 이용완료 처리 API-653
    @PostMapping("/internal/complete")
    public ResultResponse<ReservationCompleteRes> completeGxReservations() {
        return ResultResponse.success("GX 이용완료 처리 성공", gxReservationService.completeGxReservations());
    }
}
