package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.HouseholdCreateReq;
import com.apten.household.application.model.request.HouseholdHeadPatchReq;
import com.apten.household.application.model.request.HouseholdListReq;
import com.apten.household.application.model.request.HouseholdMemberPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPostReq;
import com.apten.household.application.model.request.HouseholdStatusPatchReq;
import com.apten.household.application.model.response.HouseholdCreateRes;
import com.apten.household.application.model.response.HouseholdDetailRes;
import com.apten.household.application.model.response.HouseholdHeadPatchRes;
import com.apten.household.application.model.response.HouseholdHistoryRes;
import com.apten.household.application.model.response.HouseholdListRes;
import com.apten.household.application.model.response.HouseholdMemberDeleteRes;
import com.apten.household.application.model.response.HouseholdMemberListRes;
import com.apten.household.application.model.response.HouseholdMemberPatchRes;
import com.apten.household.application.model.response.HouseholdMemberPostRes;
import com.apten.household.application.model.response.HouseholdStatusPatchRes;
import com.apten.household.application.service.HouseholdService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 세대 도메인 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminHouseholdController {

    // 세대 도메인 응용 서비스
    private final HouseholdService householdService;

    //세대 마스터 등록 API-401
    @PostMapping("/households")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdCreateRes> createHousehold(@RequestBody HouseholdCreateReq request) {
        return ResultResponse.success("세대 등록 성공", householdService.createHousehold(request));
    }

    //세대 목록 조회 API-402
    @GetMapping("/households")
    public ResultResponse<HouseholdListRes> getHouseholdList(
            @ModelAttribute HouseholdListReq request
    ) {
        return ResultResponse.success("세대 목록 조회 성공", householdService.getHouseholdList(request));
    }

    //세대 상세 조회 API-403
    @GetMapping("/households/{householdId}")
    public ResultResponse<HouseholdDetailRes> getHouseholdDetail(@PathVariable Long householdId) {
        return ResultResponse.success("세대 상세 조회 성공", householdService.getHouseholdDetail(householdId));
    }

    //세대 상태 변경 API-404
    @PatchMapping("/households/{householdId}/status")
    public ResultResponse<HouseholdStatusPatchRes> changeHouseholdStatus(
            @PathVariable Long householdId,
            @RequestBody HouseholdStatusPatchReq request
    ) {
        return ResultResponse.success("세대 상태 변경 성공", householdService.changeHouseholdStatus(householdId, request));
    }

    //입주/퇴거 이력 조회 API-405
    @GetMapping("/households/{householdId}/history")
    public ResultResponse<List<HouseholdHistoryRes>> getHouseholdHistory(@PathVariable Long householdId) {
        return ResultResponse.success("세대 이력 조회 성공", householdService.getHouseholdHistory(householdId));
    }

    //세대원 등록 API-406
    @PostMapping("/households/{householdId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdMemberPostRes> addHouseholdMember(
            @PathVariable Long householdId,
            @RequestBody HouseholdMemberPostReq request
    ) {
        return ResultResponse.success("세대원 등록 성공", householdService.addHouseholdMember(householdId, request));
    }

    //세대원 조회 API-407
    @GetMapping("/households/{householdId}/members")
    public ResultResponse<List<HouseholdMemberListRes>> getHouseholdMembers(@PathVariable Long householdId) {
        return ResultResponse.success("세대원 목록 조회 성공", householdService.getHouseholdMembers(householdId));
    }

    //세대원 수정 API-408
    @PatchMapping("/household-members/{householdMemberId}")
    public ResultResponse<HouseholdMemberPatchRes> updateHouseholdMember(
            @PathVariable Long householdMemberId,
            @RequestBody HouseholdMemberPatchReq request
    ) {
        return ResultResponse.success("세대원 수정 성공", householdService.updateHouseholdMember(householdMemberId, request));
    }

    //세대원 삭제 API-409
    @DeleteMapping("/household-members/{householdMemberId}")
    public ResultResponse<HouseholdMemberDeleteRes> deleteHouseholdMember(@PathVariable Long householdMemberId) {
        return ResultResponse.success("세대원 삭제 성공", householdService.deleteHouseholdMember(householdMemberId));
    }

    //세대주 권한 변경 API-410
    @PatchMapping("/households/{householdId}/head")
    public ResultResponse<HouseholdHeadPatchRes> changeHouseholdHead(
            @PathVariable Long householdId,
            @RequestBody HouseholdHeadPatchReq request
    ) {
        return ResultResponse.success("세대주 변경 성공", householdService.changeHouseholdHead(householdId, request));
    }
}
