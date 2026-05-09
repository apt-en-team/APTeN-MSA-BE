package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.FacilityFeeReflectReq;
import com.apten.household.application.model.request.HouseholdMatchPostReq;
import com.apten.household.application.model.request.VehicleFeeReflectReq;
import com.apten.household.application.model.request.VisitorFeeReflectReq;
import com.apten.household.application.model.response.FacilityFeeReflectRes;
import com.apten.household.application.model.response.HouseholdMatchPostRes;
import com.apten.household.application.model.response.VehicleFeeReflectRes;
import com.apten.household.application.model.response.VisitorFeeReflectRes;
import com.apten.household.application.service.HouseholdBillService;
import com.apten.household.application.service.HouseholdMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 내부 연동 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalHouseholdController {

    // 세대 매칭 도메인 응용 서비스
    private final HouseholdMatchService householdMatchService;

    // 청구 반영 도메인 응용 서비스
    private final HouseholdBillService householdBillService;

    //세대 매칭 요청 API-411
    @PostMapping("/household-match-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdMatchPostRes> createMatchRequest(
            @RequestBody HouseholdMatchPostReq request
    ) {
        return ResultResponse.success("세대 매칭 요청 생성 성공", householdMatchService.createMatchRequest(request));
    }

    //차량 비용 반영 API-417
    @PostMapping("/household-bills/vehicle-fees")
    public ResultResponse<VehicleFeeReflectRes> reflectVehicleFee(@RequestBody VehicleFeeReflectReq request) {
        return ResultResponse.success("차량 비용 반영 성공", householdBillService.reflectVehicleFee(request));
    }

    //시설 비용 반영 API-418
    @PostMapping("/household-bills/facility-fees")
    public ResultResponse<FacilityFeeReflectRes> reflectFacilityFee(@RequestBody FacilityFeeReflectReq request) {
        return ResultResponse.success("시설 비용 반영 성공", householdBillService.reflectFacilityFee(request));
    }

    //방문차량 비용 반영 API-419
    @PostMapping("/household-bills/visitor-fees")
    public ResultResponse<VisitorFeeReflectRes> reflectVisitorFee(@RequestBody VisitorFeeReflectReq request) {
        return ResultResponse.success("방문차량 비용 반영 성공", householdBillService.reflectVisitorFee(request));
    }
}
