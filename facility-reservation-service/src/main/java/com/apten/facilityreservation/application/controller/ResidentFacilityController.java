package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.ResidentFacilityListReq;
import com.apten.facilityreservation.application.model.response.ResidentFacilityDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentFacilityListRes;
import com.apten.facilityreservation.application.service.FacilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// 입주민 시설 조회 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class ResidentFacilityController {

    private final FacilityService facilityService;

    //입주민 시설 목록 조회 API-617
    @GetMapping("/api/facilities")
    public ResultResponse<List<ResidentFacilityListRes>> getResidentFacilityList(@ModelAttribute ResidentFacilityListReq req) {
        return ResultResponse.success("입주민 시설 목록 조회 성공", facilityService.getResidentFacilityList(req));
    }

    //입주민 시설 상세 조회 API-618
    @GetMapping("/api/facilities/{facilityId}")
    public ResultResponse<ResidentFacilityDetailRes> getResidentFacilityDetail(@PathVariable Long facilityId) {
        return ResultResponse.success("입주민 시설 상세 조회 성공", facilityService.getResidentFacilityDetail(facilityId));
    }
}
