package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.response.BillConfirmRes;
import com.apten.household.application.service.HouseholdBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 세대 비용 도메인 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/household-bills")
public class AdminHouseholdBillController {

    // 세대 비용 도메인 응용 서비스
    private final HouseholdBillService householdBillService;

    //월별 비용 확정 API-420
    @PatchMapping("/{billId}/confirm")
    public ResultResponse<BillConfirmRes> confirmHouseholdBill(@PathVariable Long billId) {
        return ResultResponse.success("세대 비용 확정 성공", householdBillService.confirmBill(billId));
    }
}
