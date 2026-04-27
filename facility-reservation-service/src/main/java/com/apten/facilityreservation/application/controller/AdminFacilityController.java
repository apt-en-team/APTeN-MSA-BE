package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.CountStatusReq;
import com.apten.facilityreservation.application.model.request.FacilityActivePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeListReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePostReq;
import com.apten.facilityreservation.application.model.request.FacilityListReq;
import com.apten.facilityreservation.application.model.request.FacilityPatchReq;
import com.apten.facilityreservation.application.model.request.FacilityPostReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPatchReq;
import com.apten.facilityreservation.application.model.request.FacilitySeatPostReq;
import com.apten.facilityreservation.application.model.request.FacilityTypePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityTypePostReq;
import com.apten.facilityreservation.application.model.request.FacilityUsageStatusReq;
import com.apten.facilityreservation.application.model.request.SeatStatusReq;
import com.apten.facilityreservation.application.model.response.CountStatusRes;
import com.apten.facilityreservation.application.model.response.FacilityActivePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeListRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimePostRes;
import com.apten.facilityreservation.application.model.response.FacilityDeleteRes;
import com.apten.facilityreservation.application.model.response.FacilityDetailRes;
import com.apten.facilityreservation.application.model.response.FacilityListRes;
import com.apten.facilityreservation.application.model.response.FacilityPatchRes;
import com.apten.facilityreservation.application.model.response.FacilityPostRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatListRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatPatchRes;
import com.apten.facilityreservation.application.model.response.FacilitySeatPostRes;
import com.apten.facilityreservation.application.model.response.FacilityTypeListRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityTypePostRes;
import com.apten.facilityreservation.application.model.response.FacilityUsageStatusRes;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.SeatStatusRes;
import com.apten.facilityreservation.application.service.FacilityService;
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

// 관리자 시설과 좌석, 현황 관리 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class AdminFacilityController {

    private final FacilityService facilityService;

    //시설 등록 API-601
    @PostMapping("/api/admin/facilities")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilityPostRes> createFacility(@RequestBody FacilityPostReq req) {
        return ResultResponse.success("시설 등록 성공", facilityService.createFacility(req));
    }

    //관리자 시설 목록 조회 API-602
    @GetMapping("/api/admin/facilities")
    public ResultResponse<PageResponse<FacilityListRes>> getAdminFacilityList(@ModelAttribute FacilityListReq req) {
        return ResultResponse.success("관리자 시설 목록 조회 성공", facilityService.getAdminFacilityList(req));
    }

    //관리자 시설 상세 조회 API-603
    @GetMapping("/api/admin/facilities/{facilityId}")
    public ResultResponse<FacilityDetailRes> getAdminFacilityDetail(@PathVariable Long facilityId) {
        return ResultResponse.success("관리자 시설 상세 조회 성공", facilityService.getAdminFacilityDetail(facilityId));
    }

    //시설 수정 API-604
    @PatchMapping("/api/admin/facilities/{facilityId}")
    public ResultResponse<FacilityPatchRes> updateFacility(@PathVariable Long facilityId, @RequestBody FacilityPatchReq req) {
        return ResultResponse.success("시설 수정 성공", facilityService.updateFacility(facilityId, req));
    }

    //시설 삭제 API-605
    @DeleteMapping("/api/admin/facilities/{facilityId}")
    public ResultResponse<FacilityDeleteRes> deleteFacility(@PathVariable Long facilityId) {
        return ResultResponse.success("시설 삭제 성공", facilityService.deleteFacility(facilityId));
    }

    //시설 활성/비활성 변경 API-606
    @PatchMapping("/api/admin/facilities/{facilityId}/active")
    public ResultResponse<FacilityActivePatchRes> changeFacilityActive(
            @PathVariable Long facilityId,
            @RequestBody FacilityActivePatchReq req
    ) {
        return ResultResponse.success("시설 활성 상태 변경 성공", facilityService.changeFacilityActive(facilityId, req));
    }

    //시설 타입 등록 API-607
    @PostMapping("/api/admin/facility-types")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilityTypePostRes> createFacilityType(@RequestBody FacilityTypePostReq req) {
        return ResultResponse.success("시설 타입 등록 성공", facilityService.createFacilityType(req));
    }

    //시설 타입 목록 조회 API-608
    @GetMapping("/api/admin/facility-types")
    public ResultResponse<List<FacilityTypeListRes>> getFacilityTypeList() {
        return ResultResponse.success("시설 타입 목록 조회 성공", facilityService.getFacilityTypeList());
    }

    //시설 타입 수정 API-609
    @PatchMapping("/api/admin/facility-types/{facilityTypeId}")
    public ResultResponse<FacilityTypePatchRes> updateFacilityType(
            @PathVariable Long facilityTypeId,
            @RequestBody FacilityTypePatchReq req
    ) {
        return ResultResponse.success("시설 타입 수정 성공", facilityService.updateFacilityType(facilityTypeId, req));
    }

    //시설 차단 시간 등록 API-612
    @PostMapping("/api/admin/facilities/{facilityId}/block-times")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilityBlockTimePostRes> createFacilityBlockTime(
            @PathVariable Long facilityId,
            @RequestBody FacilityBlockTimePostReq req
    ) {
        return ResultResponse.success("시설 차단 시간 등록 성공", facilityService.createFacilityBlockTime(facilityId, req));
    }

    //시설 차단 시간 조회 API-613
    @GetMapping("/api/admin/facilities/{facilityId}/block-times")
    public ResultResponse<List<FacilityBlockTimeListRes>> getFacilityBlockTimeList(
            @PathVariable Long facilityId,
            @ModelAttribute FacilityBlockTimeListReq req
    ) {
        return ResultResponse.success("시설 차단 시간 조회 성공", facilityService.getFacilityBlockTimeList(facilityId, req));
    }

    //시설 좌석 등록 API-614
    @PostMapping("/api/admin/facilities/{facilityId}/seats")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilitySeatPostRes> createFacilitySeat(
            @PathVariable Long facilityId,
            @RequestBody FacilitySeatPostReq req
    ) {
        return ResultResponse.success("시설 좌석 등록 성공", facilityService.createFacilitySeat(facilityId, req));
    }

    //시설 좌석 목록 조회 API-615
    @GetMapping("/api/admin/facilities/{facilityId}/seats")
    public ResultResponse<List<FacilitySeatListRes>> getFacilitySeatList(@PathVariable Long facilityId) {
        return ResultResponse.success("시설 좌석 목록 조회 성공", facilityService.getFacilitySeatList(facilityId));
    }

    //시설 좌석 수정 API-616
    @PatchMapping("/api/admin/facility-seats/{seatId}")
    public ResultResponse<FacilitySeatPatchRes> updateFacilitySeat(
            @PathVariable Long seatId,
            @RequestBody FacilitySeatPatchReq req
    ) {
        return ResultResponse.success("시설 좌석 수정 성공", facilityService.updateFacilitySeat(seatId, req));
    }

    //시설 이용 현황 조회 API-644
    @GetMapping("/api/admin/facility-usage/status")
    public ResultResponse<FacilityUsageStatusRes> getFacilityUsageStatus(@ModelAttribute FacilityUsageStatusReq req) {
        return ResultResponse.success("시설 이용 현황 조회 성공", facilityService.getFacilityUsageStatus(req));
    }

    //좌석 상태 조회 API-645
    @GetMapping("/api/admin/facilities/{facilityId}/seat-status")
    public ResultResponse<SeatStatusRes> getSeatStatus(@PathVariable Long facilityId, @ModelAttribute SeatStatusReq req) {
        return ResultResponse.success("좌석 상태 조회 성공", facilityService.getSeatStatus(facilityId, req));
    }

    //정원형 이용 현황 조회 API-646
    @GetMapping("/api/admin/facilities/{facilityId}/count-status")
    public ResultResponse<CountStatusRes> getCountStatus(@PathVariable Long facilityId, @ModelAttribute CountStatusReq req) {
        return ResultResponse.success("정원형 이용 현황 조회 성공", facilityService.getCountStatus(facilityId, req));
    }
}
