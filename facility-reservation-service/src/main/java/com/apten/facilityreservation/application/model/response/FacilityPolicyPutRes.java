package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.FacilityUsageUnitType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 정책 저장 응답 DTO이다.
@Getter
@Builder
public class FacilityPolicyPutRes {

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

    // 요금 청구 방식이다.
    private FacilityFeeType feeType;

    // BASE_PLUS_EXTRA 전용 — 기본 포함 인원 수이다.
    private Integer includedPersonCount;

    // BASE_PLUS_EXTRA 전용 — 초과 인원당 추가 요금이다.
    private BigDecimal extraPersonFee;

    // 구독 신청 기준일이다.
    private Integer subscribeCutoffDay;

    // 구독 해지 기준일이다.
    private Integer cancelCutoffDay;

    // 활성 여부이다.
    private Boolean isActive;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
