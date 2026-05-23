package com.apten.facilityreservation.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.response.AdminFacilitySubscriptionListRes;
import com.apten.facilityreservation.application.service.FacilitySubscriptionService;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 관리자 시설 구독 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class AdminFacilitySubscriptionController {

    private final FacilitySubscriptionService facilitySubscriptionService;

    // API-653 관리자 구독 목록 조회
    @GetMapping("/api/admin/facility-subscriptions")
    public ResultResponse<List<AdminFacilitySubscriptionListRes>> getAdminSubscriptionList(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) FacilitySubscriptionStatus status
    ) {
        return ResultResponse.success("구독 목록 조회 성공",
                facilitySubscriptionService.getAdminSubscriptionList(complexId, facilityId, status));
    }
}
