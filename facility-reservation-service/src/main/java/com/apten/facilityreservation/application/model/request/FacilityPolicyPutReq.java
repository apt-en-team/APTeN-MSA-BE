package com.apten.facilityreservation.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 정책 설정 요청 DTO이다.
// 시설 타입별 기본 요금과 예약 단위, 취소 정책을 받을 때 사용한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicyPutReq {

    // 정책을 적용할 시설 타입 코드이다.
    private String facilityTypeCode;

    // 시설 타입 기본 요금이다.
    private BigDecimal baseFee;

    // 시설 타입 기본 예약 단위이다.
    private Integer slotMin;

    // 예약 시작 몇 시간 전까지 취소 가능한지 나타내는 값이다.
    private Integer cancelDeadlineHours;

    // GX 대기 허용 여부이다.
    private Boolean gxWaitingEnabled;

    // 정책 활성 여부이다.
    private Boolean isActive;
}
