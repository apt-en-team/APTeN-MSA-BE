package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.FacilityPolicyListReq;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.application.model.response.FacilityPolicyListRes;
import com.apten.facilityreservation.application.model.response.FacilityPolicyPutRes;
import com.apten.facilityreservation.application.service.FacilityPolicyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 관리자 시설 정책 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class AdminFacilityPolicyController {

    private final FacilityPolicyService facilityPolicyService;

    //시설 예약 정책 설정 API-610
    @PutMapping("/api/admin/facility-policies")
    public ResultResponse<FacilityPolicyPutRes> updateFacilityPolicy(@RequestBody FacilityPolicyPutReq req) {
        return ResultResponse.success("시설 예약 정책 설정 성공", facilityPolicyService.updateFacilityPolicy(req));
    }

    //시설 예약 정책 조회 API-611
    @GetMapping("/api/admin/facility-policies")
    public ResultResponse<List<FacilityPolicyListRes>> getFacilityPolicyList(@ModelAttribute FacilityPolicyListReq req) {
        return ResultResponse.success("시설 예약 정책 조회 성공", facilityPolicyService.getFacilityPolicyList(req));
    }
}
