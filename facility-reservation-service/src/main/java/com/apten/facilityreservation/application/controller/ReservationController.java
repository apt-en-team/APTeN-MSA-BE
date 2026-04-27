package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.AvailableTimeListReq;
import com.apten.facilityreservation.application.model.request.MyReservationListReq;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 일반 시설 예약 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    //예약 가능 시간 조회 API-619
    @GetMapping("/api/reservations/available-times")
    public ResultResponse<List<AvailableTimeListRes>> getAvailableTimeList(@ModelAttribute AvailableTimeListReq req) {
        return ResultResponse.success("예약 가능 시간 조회 성공", reservationService.getAvailableTimeList(req));
    }

    //좌석 임시 선점 API-620
    @PostMapping("/api/reservations/seat-holds")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<SeatHoldPostRes> createSeatHold(@RequestBody SeatHoldPostReq req) {
        return ResultResponse.success("좌석 임시 선점 성공", reservationService.createSeatHold(req));
    }

    //예약 생성 API-622
    @PostMapping("/api/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ReservationPostRes> createReservation(@RequestBody ReservationPostReq req) {
        return ResultResponse.success("예약 생성 성공", reservationService.createReservation(req));
    }

    //내 예약 목록 조회 API-623
    @GetMapping("/api/reservations/my")
    public ResultResponse<PageResponse<MyReservationListRes>> getMyReservationList(@ModelAttribute MyReservationListReq req) {
        return ResultResponse.success("내 예약 목록 조회 성공", reservationService.getMyReservationList(req));
    }

    //내 예약 상세 조회 API-624
    @GetMapping("/api/reservations/{reservationId}")
    public ResultResponse<MyReservationDetailRes> getMyReservationDetail(@PathVariable Long reservationId) {
        return ResultResponse.success("내 예약 상세 조회 성공", reservationService.getMyReservationDetail(reservationId));
    }

    //예약 취소 API-625
    @PatchMapping("/api/reservations/{reservationId}/cancel")
    public ResultResponse<ReservationCancelRes> cancelReservation(
            @PathVariable Long reservationId,
            @RequestBody(required = false) ReservationCancelReq req
    ) {
        return ResultResponse.success("예약 취소 성공", reservationService.cancelReservation(reservationId, req == null ? ReservationCancelReq.builder().build() : req));
    }
}
