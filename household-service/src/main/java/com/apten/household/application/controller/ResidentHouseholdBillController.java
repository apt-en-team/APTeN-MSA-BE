package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.MyBillListReq;
import com.apten.household.application.model.response.MyBillListRes;
import com.apten.household.application.service.HouseholdBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 입주민 세대 비용 조회 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/household-bills")
public class ResidentHouseholdBillController {

    // 세대 비용 도메인 응용 서비스
    private final HouseholdBillService householdBillService;

    //세대 비용 조회 API-421
    @GetMapping
    public ResultResponse<MyBillListRes> getMyBills(@ModelAttribute MyBillListReq request) {
        return ResultResponse.success("세대 비용 조회 성공", householdBillService.getMyBills(request));
    }
}
