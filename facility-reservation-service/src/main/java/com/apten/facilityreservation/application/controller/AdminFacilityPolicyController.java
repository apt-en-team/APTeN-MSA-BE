package com.apten.facilityreservation.application.controller;

import com.apten.common.response.ResultResponse;
import com.apten.common.constants.HeaderConstants;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.model.request.FacilityPolicyListReq;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.application.model.response.FacilityPolicyListRes;
import com.apten.facilityreservation.application.model.response.FacilityPolicyPutRes;
import com.apten.facilityreservation.application.service.FacilityPolicyService;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 관리자 시설 정책 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/facility-policies")
public class AdminFacilityPolicyController {

    private final FacilityPolicyService facilityPolicyService;
    private final FacilityRequestContextResolver facilityRequestContextResolver;

    // 시설 예약 정책 설정
    @PutMapping
    public ResultResponse<FacilityPolicyPutRes> updateFacilityPolicy(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody FacilityPolicyPutReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 예약 정책 설정 성공", facilityPolicyService.updateFacilityPolicy(context.getComplexId(), req));
    }

    // 시설 예약 정책 조회
    @GetMapping
    public ResultResponse<List<FacilityPolicyListRes>> getFacilityPolicyList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @ModelAttribute FacilityPolicyListReq req
    ) {
        FacilityRequestContext context = facilityRequestContextResolver.resolveAdminContext(userId, userRole, complexId, selectedComplexId);
        return ResultResponse.success("시설 예약 정책 조회 성공", facilityPolicyService.getFacilityPolicyList(context.getComplexId(), req));
    }
}
