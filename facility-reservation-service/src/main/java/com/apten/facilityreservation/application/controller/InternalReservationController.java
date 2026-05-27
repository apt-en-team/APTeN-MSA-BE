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

    // 클래스 레벨 /internal과 중복되지 않게 메서드 경로에는 세부 동작만 둔다.
    @PostMapping("/reservation-temp-holds/expire")
    public ResultResponse<TempHoldExpireRes> expireSeatHolds() {
        return ResultResponse.success("좌석 임시 선점 자동 해제 성공", reservationService.expireSeatHolds());
    }

    // 일반 시설 예약 완료 처리는 /internal/reservations/complete로 분리한다.
    @PostMapping("/reservations/complete")
    public ResultResponse<ReservationCompleteRes> completeReservations() {
        return ResultResponse.success("예약 완료 처리 성공", reservationService.completeReservations());
    }

    // GX 예약 완료 처리는 일반 예약과 충돌하지 않게 별도 경로로 둔다.
    @PostMapping("/gx-reservations/complete")
    public ResultResponse<ReservationCompleteRes> completeGxReservations() {
        return ResultResponse.success("GX 이용완료 처리 성공", gxReservationService.completeGxReservations());
    }
}
