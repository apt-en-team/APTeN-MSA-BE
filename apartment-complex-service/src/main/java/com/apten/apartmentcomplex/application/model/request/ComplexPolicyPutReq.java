package com.apten.apartmentcomplex.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 기본 정책 설정 요청 DTO
// 기본 관리비와 예약 단위 정책 값을 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexPolicyPutReq {
    private BigDecimal defaultMaintenanceFee;
    private Integer defaultReservationSlotMin;
    private String lateFeePolicy;
    private String memo;
}
