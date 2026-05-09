package com.apten.common.kafka.payload;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// household-service의 facility_usage_snapshot 동기화를 위한 공통 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityUsageEventPayload {

    // 원본 예약 식별자이다.
    private Long reservationId;

    // 세대 식별자이다.
    private Long householdId;

    // 단지 식별자이다.
    private Long complexId;

    // 시설 식별자이다.
    private Long facilityId;

    // 시설 이름이다.
    private String facilityName;

    // 이용일이다.
    private LocalDate usageDate;

    // 이용 요금이다.
    private BigDecimal facilityFee;

    // 이용 상태이다.
    private String status;
}
