package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.request.FacilityFeeCalculateReq;
import com.apten.facilityreservation.application.model.request.FacilityFeePublishReq;
import com.apten.facilityreservation.application.model.response.FacilityFeeCalculateRes;
import com.apten.facilityreservation.application.model.response.FacilityFeePublishRes;
import com.apten.facilityreservation.application.service.FacilityFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 내부 비용 산정 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class InternalFacilityFeeController {

    private final FacilityFeeService facilityFeeService;

    //시설 이용 비용 산정 API-648
    @PostMapping("/internal/facility-fees/calculate")
    public ResultResponse<FacilityFeeCalculateRes> calculateFacilityFees(@RequestBody FacilityFeeCalculateReq req) {
        return ResultResponse.success("시설 이용 비용 산정 성공", facilityFeeService.calculateFacilityFees(req));
    }

    //시설 이용 비용 발행 API-649
    @PostMapping("/internal/facility-fees/publish")
    public ResultResponse<FacilityFeePublishRes> publishFacilityFees(@RequestBody FacilityFeePublishReq req) {
        return ResultResponse.success("시설 이용 비용 발행 성공", facilityFeeService.publishFacilityFees(req));
    }
}
