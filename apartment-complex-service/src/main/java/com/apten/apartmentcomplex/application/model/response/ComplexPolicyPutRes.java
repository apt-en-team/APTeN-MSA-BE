package com.apten.apartmentcomplex.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 기본 정책 설정 응답 DTO
// 단지 기본 관리비 정책 저장 결과를 내려줄 때 사용한다
@Getter
@Builder
public class ComplexPolicyPutRes {
    private final String apartmentComplexUid;
    private final BigDecimal defaultMaintenanceFee;
    private final Integer defaultReservationSlotMin;
    private final LocalDateTime updatedAt;
}
