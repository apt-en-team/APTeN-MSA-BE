package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.HouseholdBillSearchReq;
import com.apten.household.application.model.response.HouseholdBillRes;
import com.apten.household.application.service.HouseholdBillService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 입주민 세대 비용 조회 API 진입점
// 사용자용 비용 조회는 비용 도메인 서비스로만 연결한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ResidentHouseholdBillController {

    // 세대 비용 도메인 응용 서비스
    private final HouseholdBillService householdBillService;

    // 입주민 세대 비용 조회 API
    @GetMapping("/household-bills")
    public ResultResponse<List<HouseholdBillRes>> getMyBills(@ModelAttribute HouseholdBillSearchReq request) {
        return ResultResponse.success("세대 비용 조회 성공", householdBillService.getMyBills(request));
    }
}
