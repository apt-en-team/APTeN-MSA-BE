package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.response.HouseholdBaseResponse;
import com.apten.household.application.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// household-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 세대 관리 API는 이후 이 계층에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/households")
public class HouseholdController {

    // 세대 응용 계층 진입점
    private final HouseholdService householdService;

    // 공통 응답 포맷과 기본 라우팅 연결이 정상인지 확인하는 최소 엔드포인트
    @GetMapping("/template")
    public ResultResponse<HouseholdBaseResponse> getHouseholdTemplate() {
        return ResultResponse.success("household template ready", householdService.getHouseholdTemplate());
    }
}
