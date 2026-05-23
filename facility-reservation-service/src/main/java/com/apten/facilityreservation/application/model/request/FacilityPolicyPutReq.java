package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.FacilityUsageUnitType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 예약 정책 설정 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicyPutReq {

    // 시설 ID이다.
    private Long facilityId;

    // 기본 요금이다.
    private BigDecimal baseFee;

    // 예약 단위 타입이다.
    private FacilityUsageUnitType usageUnitType;

    // 기본 예약 단위이다.
    private Integer slotMin;

    // 취소 마감 시간이다.
    private Integer cancelDeadlineHours;

    // 최대 예약 인원이다.
    private Integer maxReservationCount;

    // 요금 청구 방식이다.
    private FacilityFeeType feeType;

    // BASE_PLUS_EXTRA 전용 — 기본 포함 인원 수이다.
    private Integer includedPersonCount;

    // BASE_PLUS_EXTRA 전용 — 초과 인원당 추가 요금이다.
    private BigDecimal extraPersonFee;
}
