package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

// 시설 구독 해지 응답 DTO이다.
@Getter
@Builder
public class FacilitySubscriptionCancelRes {

    // 구독 ID이다.
    private Long subscriptionId;

    // 해지된 시설 ID이다.
    private Long facilityId;

    // 해지 요청일이다.
    private LocalDate cancelledAt;

    // 해지 후 구독 상태이다.
    private FacilitySubscriptionStatus status;
}
