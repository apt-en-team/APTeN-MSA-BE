package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.FacilityActivePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimePostReq;
import com.apten.facilityreservation.application.model.request.FacilityBlockTimeSearchReq;
import com.apten.facilityreservation.application.model.request.FacilityPatchReq;
import com.apten.facilityreservation.application.model.request.FacilityPostReq;
import com.apten.facilityreservation.application.model.response.FacilityActivePatchRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimePostRes;
import com.apten.facilityreservation.application.model.response.FacilityBlockTimeRes;
import com.apten.facilityreservation.application.model.response.FacilityDeleteRes;
import com.apten.facilityreservation.application.model.response.FacilityPatchRes;
import com.apten.facilityreservation.application.model.response.FacilityPostRes;
import com.apten.facilityreservation.application.service.FacilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 시설 관리 API 진입점
// 시설 등록과 수정, 삭제, 활성화, 차단 시간 관리를 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
public class AdminFacilityController {

    private final FacilityService facilityService;

    @PostMapping("/api/admin/facilities")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilityPostRes> createFacility(@RequestBody FacilityPostReq request) {
        return ResultResponse.success("시설 등록 성공", facilityService.createFacility(request));
    }

    @PatchMapping("/api/admin/facilities/{facilityUid}")
    public ResultResponse<FacilityPatchRes> updateFacility(
            @PathVariable String facilityUid,
            @RequestBody FacilityPatchReq request
    ) {
        return ResultResponse.success("시설 수정 성공", facilityService.updateFacility(facilityUid, request));
    }

    @DeleteMapping("/api/admin/facilities/{facilityUid}")
    public ResultResponse<FacilityDeleteRes> deleteFacility(@PathVariable String facilityUid) {
        return ResultResponse.success("시설 삭제 성공", facilityService.deleteFacility(facilityUid));
    }

    @PatchMapping("/api/admin/facilities/{facilityUid}/active")
    public ResultResponse<FacilityActivePatchRes> changeFacilityActive(
            @PathVariable String facilityUid,
            @RequestBody FacilityActivePatchReq request
    ) {
        return ResultResponse.success("시설 활성 상태 변경 성공", facilityService.changeFacilityActive(facilityUid, request));
    }

    @PostMapping("/api/admin/facilities/{facilityUid}/block-times")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<FacilityBlockTimePostRes> createFacilityBlockTime(
            @PathVariable String facilityUid,
            @RequestBody FacilityBlockTimePostReq request
    ) {
        return ResultResponse.success("시설 차단 시간 등록 성공", facilityService.createFacilityBlockTime(facilityUid, request));
    }

    @GetMapping("/api/admin/facilities/{facilityUid}/block-times")
    public ResultResponse<List<FacilityBlockTimeRes>> getFacilityBlockTimeList(
            @PathVariable String facilityUid,
            @ModelAttribute FacilityBlockTimeSearchReq request
    ) {
        return ResultResponse.success("시설 차단 시간 조회 성공", facilityService.getFacilityBlockTimeList(facilityUid, request));
    }
}
