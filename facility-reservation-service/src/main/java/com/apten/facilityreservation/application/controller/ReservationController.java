package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.MyReservationSearchReq;
import com.apten.facilityreservation.application.model.request.ReservationAvailableTimeSearchReq;
import com.apten.facilityreservation.application.model.request.ReservationPostReq;
import com.apten.facilityreservation.application.model.request.ReservationSeatHoldPostReq;
import com.apten.facilityreservation.application.model.request.ReservationSeatSearchReq;
import com.apten.facilityreservation.application.model.response.MyReservationGetPageRes;
import com.apten.facilityreservation.application.model.response.ReservationAvailableTimeRes;
import com.apten.facilityreservation.application.model.response.ReservationCancelRes;
import com.apten.facilityreservation.application.model.response.ReservationGetDetailRes;
import com.apten.facilityreservation.application.model.response.ReservationPostRes;
import com.apten.facilityreservation.application.model.response.ReservationSeatHoldDeleteRes;
import com.apten.facilityreservation.application.model.response.ReservationSeatHoldPostRes;
import com.apten.facilityreservation.application.model.response.ReservationSeatRes;
import com.apten.facilityreservation.application.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 일반 예약 API 진입점
// 예약 생성과 좌석 선점, 내 예약 조회와 취소 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/api/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ReservationPostRes> createReservation(@RequestBody ReservationPostReq request) {
        return ResultResponse.success("예약 생성 성공", reservationService.createReservation(request));
    }

    @GetMapping("/api/reservations/available-times")
    public ResultResponse<List<ReservationAvailableTimeRes>> getAvailableTimeList(
            @ModelAttribute ReservationAvailableTimeSearchReq request
    ) {
        return ResultResponse.success("예약 가능 시간 조회 성공", reservationService.getAvailableTimeList(request));
    }

    @GetMapping("/api/reservations/seats")
    public ResultResponse<List<ReservationSeatRes>> getSeatStatusList(@ModelAttribute ReservationSeatSearchReq request) {
        return ResultResponse.success("좌석 상태 조회 성공", reservationService.getSeatStatusList(request));
    }

    @PostMapping("/api/reservations/seat-holds")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ReservationSeatHoldPostRes> createSeatHold(@RequestBody ReservationSeatHoldPostReq request) {
        return ResultResponse.success("좌석 임시선점 성공", reservationService.createSeatHold(request));
    }

    @DeleteMapping("/api/reservations/seat-holds/{holdToken}")
    public ResultResponse<ReservationSeatHoldDeleteRes> releaseSeatHold(@PathVariable String holdToken) {
        return ResultResponse.success("좌석 임시선점 해제 성공", reservationService.releaseSeatHold(holdToken));
    }

    @GetMapping("/api/reservations/my")
    public ResultResponse<MyReservationGetPageRes> getMyReservationList(@ModelAttribute MyReservationSearchReq request) {
        return ResultResponse.success("내 예약 목록 조회 성공", reservationService.getMyReservationList(request));
    }

    @GetMapping("/api/reservations/{reservationUid}")
    public ResultResponse<ReservationGetDetailRes> getReservationDetail(@PathVariable String reservationUid) {
        return ResultResponse.success("예약 상세 조회 성공", reservationService.getReservationDetail(reservationUid));
    }

    @PatchMapping("/api/reservations/{reservationUid}/cancel")
    public ResultResponse<ReservationCancelRes> cancelReservation(@PathVariable String reservationUid) {
        return ResultResponse.success("예약 취소 성공", reservationService.cancelReservation(reservationUid));
    }
}
