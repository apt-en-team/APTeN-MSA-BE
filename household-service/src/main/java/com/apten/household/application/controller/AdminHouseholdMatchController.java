package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.HouseholdMatchListReq;
import com.apten.household.application.model.request.HouseholdMatchRejectReq;
import com.apten.household.application.model.response.HouseholdMatchApproveRes;
import com.apten.household.application.model.response.HouseholdMatchListRes;
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

// 관리자 세대 매칭 도메인 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/household-match-requests")
public class AdminHouseholdMatchController {

    // 세대 매칭 도메인 응용 서비스
    private final HouseholdMatchService householdMatchService;

    //수동 승인 대상 조회 API-413
    @GetMapping
    public ResultResponse<HouseholdMatchListRes> getHouseholdMatchRequestList(
            @ModelAttribute HouseholdMatchListReq request
    ) {
        return ResultResponse.success("세대 매칭 요청 목록 조회 성공", householdMatchService.getMatchRequestList(request));
    }

    //수동 승인 처리 API-414
    @PatchMapping("/{matchRequestId}/approve")
    public ResultResponse<HouseholdMatchApproveRes> approveMatchRequest(@PathVariable Long matchRequestId) {
        return ResultResponse.success("세대 매칭 승인 성공", householdMatchService.approveMatchRequest(matchRequestId));
    }

    //수동 거절 처리 API-415
    @PatchMapping("/{matchRequestId}/reject")
    public ResultResponse<HouseholdMatchRejectRes> rejectMatchRequest(
            @PathVariable Long matchRequestId,
            @RequestBody HouseholdMatchRejectReq request
    ) {
        return ResultResponse.success("세대 매칭 거절 성공", householdMatchService.rejectMatchRequest(matchRequestId, request));
    }
}
