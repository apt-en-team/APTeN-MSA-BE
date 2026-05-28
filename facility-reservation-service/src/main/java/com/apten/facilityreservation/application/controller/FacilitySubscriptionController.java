package com.apten.facilityreservation.application.controller;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import com.apten.facilityreservation.application.model.response.FacilitySubscriptionCancelRes;
import com.apten.facilityreservation.application.model.response.ResidentFacilitySubscriptionListRes;
import com.apten.facilityreservation.application.service.FacilitySubscriptionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 시설 구독 API 진입점이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facility-subscriptions")
public class FacilitySubscriptionController {

    private final FacilitySubscriptionService facilitySubscriptionService;

    // API-652 입주민 나의 구독 목록 조회
    @GetMapping
    public ResultResponse<List<ResidentFacilitySubscriptionListRes>> getMySubscriptions(
            @RequestHeader(HeaderConstants.X_USER_ID) Long userId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(HeaderConstants.X_COMPLEX_ID) Long complexId
    ) {
        return ResultResponse.success("나의 구독 목록 조회 성공",
                facilitySubscriptionService.getMySubscriptions(userId, complexId));
    }

    // API-651 시설 구독 해지
    @PostMapping("/{facilityId}/cancel")
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
