package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 시설 정책 설정 요청 DTO
// 시설 예약 단위와 취소 정책 값을 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicyPutReq {

    // 시설 타입 코드: STUDY_ROOM, GYM, GOLF, GX
    private String facilityTypeCode;
    // 시설 타입 기본 요금
    private BigDecimal baseFee;
    // 시설 타입 기본 예약 단위
    private Integer slotMin;
    // 예약 시작 몇 시간 전까지 취소 가능
    private Integer cancelDeadlineHours;
    // GX 대기 허용 여부
    private Boolean gxWaitingEnabled;
}
