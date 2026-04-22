package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.HouseholdMatchPostReq;
import com.apten.household.application.model.response.HouseholdMatchPostRes;
import com.apten.household.application.service.HouseholdMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 회원가입 흐름에서 호출하는 내부 세대 매칭 API 진입점
// 내부 연동 요청은 매칭 도메인 서비스로만 연결한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalHouseholdController {

    // 세대 매칭 도메인 응용 서비스
    private final HouseholdMatchService householdMatchService;

    // 세대 매칭 요청 생성 API
    @PostMapping("/household-match-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdMatchPostRes> createMatchRequest(
            @RequestBody HouseholdMatchPostReq request
    ) {
        return ResultResponse.success("세대 매칭 요청 생성 성공", householdMatchService.createMatchRequest(request));
    }
}
