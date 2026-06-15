package com.apten.household.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.HouseholdBulkCreateReq;
import com.apten.household.application.model.request.HouseholdCreateReq;
import com.apten.household.application.model.request.HouseholdHeadPatchReq;
import com.apten.household.application.model.request.HouseholdListReq;
import com.apten.household.application.model.request.HouseholdMemberPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPostReq;
import com.apten.household.application.model.request.HouseholdPatchReq;
import com.apten.household.application.model.request.HouseholdStatusPatchReq;
import com.apten.household.application.model.response.HouseholdBulkCreateRes;
import com.apten.household.application.model.response.HouseholdCreateRes;
import com.apten.household.application.model.response.HouseholdDetailRes;
import com.apten.household.application.model.response.HouseholdHeadPatchRes;
import com.apten.household.application.model.response.HouseholdHistoryRes;
import com.apten.household.application.model.response.HouseholdListRes;
import com.apten.household.application.model.response.HouseholdPatchRes;
import com.apten.household.application.model.response.HouseholdMemberDeleteRes;
import com.apten.household.application.model.response.HouseholdMemberListRes;
import com.apten.household.application.model.response.HouseholdMemberPatchRes;
import com.apten.household.application.model.response.HouseholdMemberPostRes;
import com.apten.household.application.model.response.HouseholdMemberRepublishRes;
import com.apten.household.application.model.response.HouseholdRepublishRes;
import com.apten.household.application.model.response.HouseholdStatusPatchRes;
import com.apten.household.application.service.HouseholdRequestContextResolver;
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
import org.springframework.web.bind.annotation.RequestHeader;
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

    // 관리자 헤더 기준 단지 컨텍스트를 해석한다.
    private final HouseholdRequestContextResolver householdRequestContextResolver;

    //세대 마스터 등록 API-401
    @PostMapping("/households")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdCreateRes> createHousehold(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody @jakarta.validation.Valid HouseholdCreateReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 등록 성공", householdService.createHousehold(context.getComplexId(), request));
    }

    @PostMapping("/households/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdBulkCreateRes> createHouseholdsBulk(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody HouseholdBulkCreateReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 일괄 등록 성공", householdService.createHouseholdsBulk(context.getComplexId(), request));
    }

    //세대 목록 조회 API-402
    @GetMapping("/households")
    public ResultResponse<HouseholdListRes> getHouseholdList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute HouseholdListReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 목록 조회 성공", householdService.getHouseholdList(context.getComplexId(), request));
    }

    //세대 상세 조회 API-403
    @GetMapping("/households/{householdId}")
    public ResultResponse<HouseholdDetailRes> getHouseholdDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 상세 조회 성공", householdService.getHouseholdDetail(context.getComplexId(), householdId));
    }

    //세대 정보 수정 API-422
    @PatchMapping("/households/{householdId}")
    public ResultResponse<HouseholdPatchRes> updateHousehold(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId,
            @RequestBody HouseholdPatchReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 정보 수정 성공", householdService.updateHousehold(context.getComplexId(), householdId, request));
    }

    //세대 삭제 API
    @DeleteMapping("/households/{householdId}")
    public ResultResponse<String> deleteHousehold(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        householdService.deleteHousehold(context.getComplexId(), householdId);
        return ResultResponse.success("세대 삭제 성공", null);
    }

    //세대 상태 변경 API-404
    @PatchMapping("/households/{householdId}/status")
    public ResultResponse<HouseholdStatusPatchRes> changeHouseholdStatus(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId,
            @RequestBody HouseholdStatusPatchReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 상태 변경 성공", householdService.changeHouseholdStatus(context.getComplexId(), householdId, request));
    }

    //입주/퇴거 이력 조회 API-405
    @GetMapping("/households/{householdId}/history")
    public ResultResponse<List<HouseholdHistoryRes>> getHouseholdHistory(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대 이력 조회 성공", householdService.getHouseholdHistory(context.getComplexId(), householdId));
    }

    //세대원 등록 API-406
    @PostMapping("/households/{householdId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdMemberPostRes> addHouseholdMember(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId,
            @RequestBody HouseholdMemberPostReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대원 등록 성공", householdService.addHouseholdMember(context.getComplexId(), householdId, request));
    }

    //세대원 조회 API-407
    @GetMapping("/households/{householdId}/members")
    public ResultResponse<List<HouseholdMemberListRes>> getHouseholdMembers(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대원 목록 조회 성공", householdService.getHouseholdMembers(context.getComplexId(), householdId));
    }

    //세대원 수정 API-408
    @PatchMapping("/household-members/{householdMemberId}")
    public ResultResponse<HouseholdMemberPatchRes> updateHouseholdMember(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdMemberId,
            @RequestBody HouseholdMemberPatchReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대원 수정 성공", householdService.updateHouseholdMember(context.getComplexId(), householdMemberId, request));
    }

    //세대원 삭제 API-409
    @DeleteMapping("/household-members/{householdMemberId}")
    public ResultResponse<HouseholdMemberDeleteRes> deleteHouseholdMember(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdMemberId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대원 삭제 성공", householdService.deleteHouseholdMember(context.getComplexId(), householdMemberId));
    }

    //세대주 권한 변경 API-410
    @PatchMapping("/households/{householdId}/head")
    public ResultResponse<HouseholdHeadPatchRes> changeHouseholdHead(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long householdId,
            @RequestBody HouseholdHeadPatchReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("세대주 변경 성공", householdService.changeHouseholdHead(context.getComplexId(), householdId, request));
    }

    // 전 세대원 이벤트 재발행 API
    @PostMapping("/household-members/republish")
    public ResultResponse<HouseholdMemberRepublishRes> republishHouseholdMembers(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole
    ) {
        // 전 단지 세대원을 대상으로 하는 전역 1회성 백필이라 단지 컨텍스트 해석 없이 처리한다.
        return ResultResponse.success("전 세대원 이벤트 재발행 성공", householdService.republishAllHouseholdMembers());
    }

    // 전 세대 이벤트 재발행 API — HouseholdCache 동호 정보 일괄 복구용
    @PostMapping("/households/republish")
    public ResultResponse<HouseholdRepublishRes> republishHouseholds(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole
    ) {
        return ResultResponse.success("전 세대 이벤트 재발행 성공", householdService.republishAllHouseholds());
    }
}
