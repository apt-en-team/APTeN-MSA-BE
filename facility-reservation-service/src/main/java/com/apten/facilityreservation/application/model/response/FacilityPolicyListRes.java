package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityUsageUnitType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 정책 조회 응답 DTO이다.
@Getter
@Builder
public class FacilityPolicyListRes {

    // 정책 ID이다.
    private Long facilityPolicyId;

    // 소속 단지 ID이다.
    private Long complexId;

    // 시설 ID이다.
    private Long facilityId;

    // 기본 요금이다.
    private BigDecimal baseFee;

    // 예약 단위 타입이다.
    private FacilityUsageUnitType usageUnitType;

    // 예약 단위이다.
    private Integer slotMin;

    // 취소 마감 시간이다.
    private Integer cancelDeadlineHours;

    // 최대 예약 인원이다.
    private Integer maxReservationCount;

    // 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
