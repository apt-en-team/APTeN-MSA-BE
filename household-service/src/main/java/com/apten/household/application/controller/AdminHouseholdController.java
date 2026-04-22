package com.apten.household.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.request.HouseholdHeadPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPostReq;
import com.apten.household.application.model.request.HouseholdPostReq;
import com.apten.household.application.model.request.HouseholdSearchReq;
import com.apten.household.application.model.request.HouseholdStatusPatchReq;
import com.apten.household.application.model.response.HouseholdGetDetailRes;
import com.apten.household.application.model.response.HouseholdGetRes;
import com.apten.household.application.model.response.HouseholdHeadPatchRes;
import com.apten.household.application.model.response.HouseholdHistoryRes;
import com.apten.household.application.model.response.HouseholdMemberDeleteRes;
import com.apten.household.application.model.response.HouseholdMemberPatchRes;
import com.apten.household.application.model.response.HouseholdMemberPostRes;
import com.apten.household.application.model.response.HouseholdMemberRes;
import com.apten.household.application.model.response.HouseholdPostRes;
import com.apten.household.application.model.response.HouseholdStatusPatchRes;
import com.apten.household.application.model.response.PageResponse;
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

// 관리자 세대 도메인 API 진입점
// 세대 마스터와 세대원 관리 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminHouseholdController {

    // 세대 도메인 응용 서비스
    private final HouseholdService householdService;

    // 세대 마스터 등록 API
    @PostMapping("/households")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdPostRes> createHousehold(@RequestBody HouseholdPostReq request) {
        return ResultResponse.success("세대 등록 성공", householdService.createHousehold(request));
    }

    // 세대 목록 조회 API
    @GetMapping("/households")
    public ResultResponse<PageResponse<HouseholdGetRes>> getHouseholdList(
            @ModelAttribute HouseholdSearchReq request
    ) {
        return ResultResponse.success("세대 목록 조회 성공", householdService.getHouseholdList(request));
    }

    // 세대 상세 조회 API
    @GetMapping("/households/{householdUid}")
    public ResultResponse<HouseholdGetDetailRes> getHouseholdDetail(@PathVariable String householdUid) {
        return ResultResponse.success("세대 상세 조회 성공", householdService.getHouseholdDetail(householdUid));
    }

    // 세대 상태 변경 API
    @PatchMapping("/households/{householdUid}/status")
    public ResultResponse<HouseholdStatusPatchRes> changeHouseholdStatus(
            @PathVariable String householdUid,
            @RequestBody HouseholdStatusPatchReq request
    ) {
        return ResultResponse.success("세대 상태 변경 성공", householdService.changeHouseholdStatus(householdUid, request));
    }

    // 세대 상태 이력 조회 API
    @GetMapping("/households/{householdUid}/history")
    public ResultResponse<List<HouseholdHistoryRes>> getHouseholdHistory(@PathVariable String householdUid) {
        return ResultResponse.success("세대 이력 조회 성공", householdService.getHouseholdHistory(householdUid));
    }

    // 세대원 등록 API
    @PostMapping("/households/{householdUid}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdMemberPostRes> addHouseholdMember(
            @PathVariable String householdUid,
            @RequestBody HouseholdMemberPostReq request
    ) {
        return ResultResponse.success("세대원 등록 성공", householdService.addHouseholdMember(householdUid, request));
    }

    // 세대원 목록 조회 API
    @GetMapping("/households/{householdUid}/members")
    public ResultResponse<List<HouseholdMemberRes>> getHouseholdMembers(@PathVariable String householdUid) {
        return ResultResponse.success("세대원 목록 조회 성공", householdService.getHouseholdMembers(householdUid));
    }

    // 세대원 수정 API
    @PatchMapping("/households/members/{householdMemberUid}")
    public ResultResponse<HouseholdMemberPatchRes> updateHouseholdMember(
            @PathVariable String householdMemberUid,
            @RequestBody HouseholdMemberPatchReq request
    ) {
        return ResultResponse.success("세대원 수정 성공", householdService.updateHouseholdMember(householdMemberUid, request));
    }

    // 세대원 삭제 API
    @DeleteMapping("/households/members/{householdMemberUid}")
    public ResultResponse<HouseholdMemberDeleteRes> deleteHouseholdMember(@PathVariable String householdMemberUid) {
        return ResultResponse.success("세대원 삭제 성공", householdService.deleteHouseholdMember(householdMemberUid));
    }

    // 세대주 변경 API
    @PatchMapping("/households/{householdUid}/head")
    public ResultResponse<HouseholdHeadPatchRes> changeHouseholdHead(
            @PathVariable String householdUid,
            @RequestBody HouseholdHeadPatchReq request
    ) {
        return ResultResponse.success("세대주 변경 성공", householdService.changeHouseholdHead(householdUid, request));
    }
}
