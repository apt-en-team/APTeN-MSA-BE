package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.HouseholdMatchRejectReq;
import com.apten.household.application.model.request.HouseholdMatchSearchReq;
import com.apten.household.application.model.response.HouseholdMatchApproveRes;
import com.apten.household.application.model.response.HouseholdMatchGetRes;
import com.apten.household.application.model.response.PageResponse;
import com.apten.household.application.model.response.HouseholdMatchRejectRes;
import com.apten.household.application.service.HouseholdMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자 세대 매칭 도메인 API 진입점
// 매칭 요청 목록과 승인 처리 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminHouseholdMatchController {

    // 세대 매칭 도메인 응용 서비스
    private final HouseholdMatchService householdMatchService;

    // 수동 승인 대상 목록 조회 API
    @GetMapping("/household-match-requests")
    public ResultResponse<PageResponse<HouseholdMatchGetRes>> getHouseholdMatchRequestList(
            @ModelAttribute HouseholdMatchSearchReq request
    ) {
        return ResultResponse.success("세대 매칭 요청 목록 조회 성공", householdMatchService.getMatchRequestList(request));
    }

    // 수동 승인 처리 API
    @PatchMapping("/household-match-requests/{matchRequestUid}/approve")
    public ResultResponse<HouseholdMatchApproveRes> approveMatchRequest(@PathVariable String matchRequestUid) {
        return ResultResponse.success("세대 매칭 승인 성공", householdMatchService.approveMatchRequest(matchRequestUid));
    }

    // 수동 거절 처리 API
    @PatchMapping("/household-match-requests/{matchRequestUid}/reject")
    public ResultResponse<HouseholdMatchRejectRes> rejectMatchRequest(
            @PathVariable String matchRequestUid,
            @RequestBody HouseholdMatchRejectReq request
    ) {
        return ResultResponse.success("세대 매칭 거절 성공", householdMatchService.rejectMatchRequest(matchRequestUid, request));
    }
}
