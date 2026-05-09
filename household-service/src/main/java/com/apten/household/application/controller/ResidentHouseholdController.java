package com.apten.household.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.response.MyHouseholdRes;
import com.apten.household.application.service.HouseholdRequestContextResolver;
import com.apten.household.application.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 입주민 세대 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/households")
public class ResidentHouseholdController {

    // 세대 도메인 응용 서비스이다.
    private final HouseholdService householdService;

    // 입주민 헤더 기준 단지 컨텍스트를 해석한다.
    private final HouseholdRequestContextResolver householdRequestContextResolver;

    //내 세대 정보 조회 API-425
    @GetMapping("/me")
    public ResultResponse<MyHouseholdRes> getMyHousehold(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("내 세대 정보 조회 성공", householdService.getMyHousehold(context.getUserId(), context.getComplexId()));
    }
}
