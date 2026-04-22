package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.FacilitySearchReq;
import com.apten.facilityreservation.application.model.response.FacilityGetDetailRes;
import com.apten.facilityreservation.application.model.response.FacilityGetRes;
import com.apten.facilityreservation.application.model.response.FacilityTypeRes;
import com.apten.facilityreservation.application.service.FacilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 시설 조회 API 진입점
// 시설 타입과 시설 목록, 상세 조회를 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping("/api/facility-types")
    public ResultResponse<List<FacilityTypeRes>> getFacilityTypeList() {
        return ResultResponse.success("시설 유형 목록 조회 성공", facilityService.getFacilityTypeList());
    }

    @GetMapping("/api/facilities")
    public ResultResponse<List<FacilityGetRes>> getFacilityList(@ModelAttribute FacilitySearchReq request) {
        return ResultResponse.success("시설 목록 조회 성공", facilityService.getFacilityList(request));
    }

    @GetMapping("/api/facilities/{facilityUid}")
    public ResultResponse<FacilityGetDetailRes> getFacilityDetail(@PathVariable String facilityUid) {
        return ResultResponse.success("시설 상세 조회 성공", facilityService.getFacilityDetail(facilityUid));
    }
}
