package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.GxBulkApproveReq;
import com.apten.facilityreservation.application.model.request.GxReservationPostReq;
import com.apten.facilityreservation.application.model.request.GxWaitingSearchReq;
import com.apten.facilityreservation.application.model.request.MyGxReservationSearchReq;
import com.apten.facilityreservation.application.model.response.GxBulkApproveRes;
import com.apten.facilityreservation.application.model.response.GxReservationCancelRes;
import com.apten.facilityreservation.application.model.response.GxReservationPostRes;
import com.apten.facilityreservation.application.model.response.GxWaitingPageRes;
import com.apten.facilityreservation.application.model.response.MyGxReservationGetPageRes;
import com.apten.facilityreservation.application.service.GxReservationService;
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

// GX 예약 API 진입점
// 사용자 GX 예약과 관리자 대기 목록, 일괄 승인 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
public class AdminGxReservationController {

    private final GxReservationService gxReservationService;

    @PostMapping("/api/gx-reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<GxReservationPostRes> createGxReservation(@RequestBody GxReservationPostReq request) {
        return ResultResponse.success("GX 예약 신청 성공", gxReservationService.createGxReservation(request));
    }

    @GetMapping("/api/gx-reservations/my")
    public ResultResponse<MyGxReservationGetPageRes> getMyGxReservationList(
            @ModelAttribute MyGxReservationSearchReq request
    ) {
        return ResultResponse.success("내 GX 예약 목록 조회 성공", gxReservationService.getMyGxReservationList(request));
    }

    @PatchMapping("/api/gx-reservations/{gxReservationUid}/cancel")
    public ResultResponse<GxReservationCancelRes> cancelGxReservation(@PathVariable String gxReservationUid) {
        return ResultResponse.success("GX 예약 취소 성공", gxReservationService.cancelGxReservation(gxReservationUid));
    }

    @GetMapping("/api/admin/gx-reservations/waiting")
    public ResultResponse<GxWaitingPageRes> getGxWaitingList(@ModelAttribute GxWaitingSearchReq request) {
        return ResultResponse.success("GX 승인 대기 목록 조회 성공", gxReservationService.getGxWaitingList(request));
    }

    @PostMapping("/api/admin/gx-reservations/bulk-approve")
    public ResultResponse<GxBulkApproveRes> bulkApproveGxReservation(@RequestBody GxBulkApproveReq request) {
        return ResultResponse.success("GX 일괄 승인 처리 성공", gxReservationService.bulkApproveGxReservation(request));
    }
}
