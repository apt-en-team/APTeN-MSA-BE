package com.apten.household.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 차량 비용 반영 응답 DTO이다.
@Getter
@Builder
public class VehicleFeeReflectRes {

    // 단지 ID이다.
    private Long complexId;

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;

    // 반영 대상 세대 수이다.
    private Integer affectedHouseholdCount;

    // 반영 시각이다.
    private LocalDateTime reflectedAt;
}
