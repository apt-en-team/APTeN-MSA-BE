package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.response.MyHouseholdRes;
import com.apten.household.application.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 입주민 세대 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/households")
public class ResidentHouseholdController {

    // 세대 도메인 응용 서비스이다.
    private final HouseholdService householdService;

    //내 세대 정보 조회 API-425
    @GetMapping("/me")
    public ResultResponse<MyHouseholdRes> getMyHousehold() {
        return ResultResponse.success("내 세대 정보 조회 성공", householdService.getMyHousehold());
    }
}
