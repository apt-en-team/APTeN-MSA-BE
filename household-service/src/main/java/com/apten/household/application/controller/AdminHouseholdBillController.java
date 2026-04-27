package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.HouseholdBillCalcReq;
import com.apten.household.application.model.response.HouseholdBillCalcRes;
import com.apten.household.application.model.response.HouseholdBillConfirmRes;
import com.apten.household.application.service.HouseholdBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

// 관리자 세대 비용 도메인 API 진입점이다.
// 청구 계산과 확정 요청만 이 컨트롤러가 받는다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminHouseholdBillController {

    // 세대 비용 도메인 응용 서비스
    private final HouseholdBillService householdBillService;

    // 차량 비용 산정 실행 API
    @PostMapping("/household-bills/calculate/vehicle")
    public ResultResponse<HouseholdBillCalcRes> calculateVehicleBill(
            @RequestBody HouseholdBillCalcReq request
    ) {
        return ResultResponse.success("차량 비용 산정 성공", householdBillService.calculateVehicleBill(request));
    }

    // 시설 비용 반영 실행 API
    @PostMapping("/household-bills/calculate/facility")
    public ResultResponse<HouseholdBillCalcRes> calculateFacilityBill(
            @RequestBody HouseholdBillCalcReq request
    ) {
        return ResultResponse.success("시설 비용 반영 성공", householdBillService.calculateFacilityBill(request));
    }

    // 방문차량 이용시간 반영 실행 API
    @PostMapping("/household-bills/calculate/visitor-usage")
    public ResultResponse<HouseholdBillCalcRes> calculateVisitorUsageBill(
            @RequestBody HouseholdBillCalcReq request
    ) {
        return ResultResponse.success("방문차량 이용시간 반영 성공", householdBillService.calculateVisitorUsageBill(request));
    }

    // 월별 비용 확정 API
    @PatchMapping("/household-bills/{billUid}/confirm")
    public ResultResponse<HouseholdBillConfirmRes> confirmHouseholdBill(@PathVariable String billUid) {
        return ResultResponse.success("세대 비용 확정 성공", householdBillService.confirmHouseholdBill(billUid));
    }
}
