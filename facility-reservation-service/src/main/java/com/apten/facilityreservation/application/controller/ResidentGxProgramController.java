package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.ResidentGxProgramListReq;
import com.apten.facilityreservation.application.model.response.PageResponse;
import com.apten.facilityreservation.application.model.response.ResidentGxProgramDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentGxProgramListRes;
import com.apten.facilityreservation.application.service.GxProgramService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// 입주민 GX 프로그램 조회 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class ResidentGxProgramController {

    private final GxProgramService gxProgramService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // API-635 입주민 GX 프로그램 목록 조회
    @GetMapping("/api/gx-programs")
    public ResultResponse<PageResponse<ResidentGxProgramListRes>> getResidentGxProgramList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @ModelAttribute ResidentGxProgramListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("입주민 GX 프로그램 목록 조회 성공", gxProgramService.getResidentGxProgramList(context.getComplexId(), req));
    }

    // API-636 입주민 GX 프로그램 상세 조회
    @GetMapping("/api/gx-programs/{programId}")
    public ResultResponse<ResidentGxProgramDetailRes> getResidentGxProgramDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long programId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("입주민 GX 프로그램 상세 조회 성공", gxProgramService.getResidentGxProgramDetail(context.getUserId(), context.getComplexId(), programId));
    }
}
