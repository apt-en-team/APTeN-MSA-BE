package com.apten.facilityreservation.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.response.FacilitySubscriptionCancelRes;
import com.apten.facilityreservation.application.service.FacilitySubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// 시설 구독 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class FacilitySubscriptionController {

    private final FacilitySubscriptionService facilitySubscriptionService;

    // API-651 시설 구독 해지
    @PostMapping("/api/facility-subscriptions/{facilityId}/cancel")
    public ResultResponse<FacilitySubscriptionCancelRes> cancelSubscription(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId,
            @PathVariable Long facilityId
    ) {
        return ResultResponse.success("시설 구독 해지 성공",
                facilitySubscriptionService.cancelSubscription(userId, complexId, facilityId));
    }
}
