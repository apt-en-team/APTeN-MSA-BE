package com.apten.apartmentcomplex.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 방문차량 정책 설정 응답 DTO
// 단지 방문차량 정책 저장 결과를 내려줄 때 사용한다
@Getter
@Builder
public class VisitorPolicyPutRes {
    private final String apartmentComplexUid;
    private final Integer freeMinutes;
    private final BigDecimal extraFeePerUnit;
    private final Integer extraFeeUnitMinutes;
    private final BigDecimal dailyMaxFee;
    private final LocalDateTime updatedAt;
}
