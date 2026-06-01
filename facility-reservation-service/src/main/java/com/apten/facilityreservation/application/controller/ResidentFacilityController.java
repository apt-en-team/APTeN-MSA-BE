package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.ResidentFacilityListReq;
import com.apten.facilityreservation.application.model.request.SeatStatusReq;
import com.apten.facilityreservation.application.model.response.ResidentFacilityDetailRes;
import com.apten.facilityreservation.application.model.response.ResidentFacilityListRes;
import com.apten.facilityreservation.application.model.response.ResidentSeatStatusRes;
import com.apten.facilityreservation.application.service.FacilityService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


// 입주민 시설 조회 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facilities")
public class ResidentFacilityController {

    private final FacilityService facilityService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // 입주민 시설 목록 조회
    @GetMapping
    public ResultResponse<List<ResidentFacilityListRes>> getResidentFacilityList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @ModelAttribute ResidentFacilityListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("입주민 시설 목록 조회 성공", facilityService.getResidentFacilityList(context.getComplexId(), req));
    }

    // 입주민 시설 상세 조회
    @GetMapping("/{facilityId}")
    public ResultResponse<ResidentFacilityDetailRes> getResidentFacilityDetail(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long facilityId
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("입주민 시설 상세 조회 성공", facilityService.getResidentFacilityDetail(context.getComplexId(), facilityId));
    }

    // 입주민 좌석 상태 조회 (개인정보 제외)
    @GetMapping("/{facilityId}/seat-status")
    public ResultResponse<List<ResidentSeatStatusRes>> getResidentSeatStatus(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long facilityId,
            @ModelAttribute SeatStatusReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveResidentContext(userId, userRole, complexId);
        return ResultResponse.success("입주민 좌석 상태 조회 성공", facilityService.getResidentSeatStatus(context.getComplexId(), facilityId, req));
    }
}
