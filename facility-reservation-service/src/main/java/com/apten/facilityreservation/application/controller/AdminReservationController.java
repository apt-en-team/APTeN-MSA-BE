package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.AdminReservationCancelReq;
import com.apten.facilityreservation.application.model.request.AdminReservationSearchReq;
import com.apten.facilityreservation.application.model.request.FacilityStatusSearchReq;
import com.apten.facilityreservation.application.model.request.GolfStatusSearchReq;
import com.apten.facilityreservation.application.model.request.GymStatusSearchReq;
import com.apten.facilityreservation.application.model.request.StudyRoomStatusSearchReq;
import com.apten.facilityreservation.application.model.response.AdminReservationCancelRes;
import com.apten.facilityreservation.application.model.response.AdminReservationGetPageRes;
import com.apten.facilityreservation.application.model.response.FacilityStatusRes;
import com.apten.facilityreservation.application.model.response.GolfStatusRes;
import com.apten.facilityreservation.application.model.response.GymStatusRes;
import com.apten.facilityreservation.application.model.response.StudyRoomSeatStatusRes;
import com.apten.facilityreservation.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 관리자 예약 관리 API 진입점
// 예약 목록과 강제 취소, 시설별 이용 현황 조회를 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping("/api/admin/reservations")
    public ResultResponse<AdminReservationGetPageRes> getAdminReservationList(
            @ModelAttribute AdminReservationSearchReq request
    ) {
        return ResultResponse.success("관리자 예약 목록 조회 성공", reservationService.getAdminReservationList(request));
    }

    @PatchMapping("/api/admin/reservations/{reservationUid}/cancel")
    public ResultResponse<AdminReservationCancelRes> cancelReservationByAdmin(
            @PathVariable String reservationUid,
            @RequestBody AdminReservationCancelReq request
    ) {
        return ResultResponse.success("관리자 예약 취소 성공", reservationService.cancelReservationByAdmin(reservationUid, request));
    }

    @GetMapping("/api/admin/reservations/facility-status")
    public ResultResponse<FacilityStatusRes> getFacilityStatus(@ModelAttribute FacilityStatusSearchReq request) {
        return ResultResponse.success("시설별 예약 현황 조회 성공", reservationService.getFacilityStatus(request));
    }

    @GetMapping("/api/admin/reservations/status/study-room")
    public ResultResponse<StudyRoomSeatStatusRes> getStudyRoomStatus(@ModelAttribute StudyRoomStatusSearchReq request) {
        return ResultResponse.success("독서실 좌석 현황 조회 성공", reservationService.getStudyRoomStatus(request));
    }

    @GetMapping("/api/admin/reservations/status/golf")
    public ResultResponse<GolfStatusRes> getGolfStatus(@ModelAttribute GolfStatusSearchReq request) {
        return ResultResponse.success("골프 좌석 현황 조회 성공", reservationService.getGolfStatus(request));
    }

    @GetMapping("/api/admin/reservations/status/gym")
    public ResultResponse<GymStatusRes> getGymStatus(@ModelAttribute GymStatusSearchReq request) {
        return ResultResponse.success("헬스장 현황 조회 성공", reservationService.getGymStatus(request));
    }
}
