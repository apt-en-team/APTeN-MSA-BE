package com.apten.household.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.BuildingLineTypeCreateReq;
import com.apten.household.application.model.request.BuildingLineTypePatchReq;
import com.apten.household.application.model.request.HouseholdTypeCreateReq;
import com.apten.household.application.model.request.HouseholdTypePatchReq;
import com.apten.household.application.model.response.BuildingLineTypeRes;
import com.apten.household.application.model.response.HouseholdTypeRes;
import com.apten.household.application.service.HouseholdRequestContextResolver;
import com.apten.household.application.service.HouseholdTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminHouseholdTypeController {

    private final HouseholdTypeService householdTypeService;

    private final HouseholdRequestContextResolver householdRequestContextResolver;

    @PostMapping("/household-types")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdTypeRes> createHouseholdType(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody HouseholdTypeCreateReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("평형 등록 성공", householdTypeService.createHouseholdType(context, request));
    }

    @GetMapping("/household-types")
    public ResultResponse<List<HouseholdTypeRes>> getHouseholdTypes(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("평형 목록 조회 성공", householdTypeService.getHouseholdTypes());
    }

    @PatchMapping("/household-types/{typeId}")
    public ResultResponse<HouseholdTypeRes> updateHouseholdType(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long typeId,
            @RequestBody HouseholdTypePatchReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("평형 수정 성공", householdTypeService.updateHouseholdType(context, typeId, request));
    }

    @DeleteMapping("/household-types/{typeId}")
    public ResultResponse<HouseholdTypeRes> deleteHouseholdType(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long typeId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("평형 삭제 성공", householdTypeService.deleteHouseholdType(context, typeId));
    }

    @PostMapping("/building-line-types")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<BuildingLineTypeRes> createBuildingLineType(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody BuildingLineTypeCreateReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("동 라인 평형 등록 성공", householdTypeService.createBuildingLineType(context, request));
    }

    @GetMapping("/building-line-types")
    public ResultResponse<List<BuildingLineTypeRes>> getBuildingLineTypes(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestParam(required = false) String building
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("동 라인 평형 목록 조회 성공", householdTypeService.getBuildingLineTypes(context, building));
    }

    @GetMapping("/building-line-types/resolve")
    public ResultResponse<BuildingLineTypeRes> resolveBuildingLineType(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestParam String building,
            @RequestParam String unit
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("동호수 평형 조회 성공", householdTypeService.resolveBuildingLineType(context, building, unit));
    }

    @PatchMapping("/building-line-types/{lineTypeId}")
    public ResultResponse<BuildingLineTypeRes> updateBuildingLineType(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long lineTypeId,
            @RequestBody BuildingLineTypePatchReq request
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("동 라인 평형 수정 성공", householdTypeService.updateBuildingLineType(context, lineTypeId, request));
    }

    @DeleteMapping("/building-line-types/{lineTypeId}")
    public ResultResponse<BuildingLineTypeRes> deleteBuildingLineType(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long lineTypeId
    ) {
        HouseholdRequestContext context = householdRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("동 라인 평형 삭제 성공", householdTypeService.deleteBuildingLineType(context, lineTypeId));
    }
}
