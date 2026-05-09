package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.ResidentFacilityListReq;
import com.apten.facilityreservation.application.model.response.ResidentFacilityDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentFacilityListRes;
import com.apten.facilityreservation.application.service.FacilityService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// 입주민 시설 조회 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class ResidentFacilityController {

    private final FacilityService facilityService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // API-617 입주민 시설 목록 조회
    @GetMapping("/api/facilities")
    public ResultResponse<List<ResidentFacilityListRes>> getResidentFacilityList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @ModelAttribute ResidentFacilityListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("입주민 시설 목록 조회 성공", facilityService.getResidentFacilityList(context.getComplexId(), req));
    }

    // API-618 입주민 시설 상세 조회
    @GetMapping("/api/facilities/{facilityId}")
    public ResultResponse<ResidentFacilityDetailRes> getResidentFacilityDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long facilityId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("입주민 시설 상세 조회 성공", facilityService.getResidentFacilityDetail(context.getComplexId(), facilityId));
    }
}
