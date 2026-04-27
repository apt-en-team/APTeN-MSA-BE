package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
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

    // 단지 ID이다.
    private Long complexId;

    // 시설 타입 코드이다.
    private FacilityTypeCode facilityTypeCode;

    // 기본 요금이다.
    private BigDecimal baseFee;

    // 기본 예약 단위이다.
    private Integer slotMin;

    // 취소 마감 시간이다.
    private Integer cancelDeadlineHours;

    // GX 대기 허용 여부이다.
    private Boolean gxWaitingEnabled;

    // 활성 여부이다.
    private Boolean isActive;
}
