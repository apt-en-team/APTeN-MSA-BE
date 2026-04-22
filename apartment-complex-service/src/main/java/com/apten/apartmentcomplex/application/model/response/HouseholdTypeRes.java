package com.apten.apartmentcomplex.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 유형 조회 응답 DTO
// 목록 조회에서 각 세대 유형의 표시 정보를 담는다
@Getter
@Builder
public class HouseholdTypeRes {
    private final Long householdTypeId;
    private final String householdTypeUid;
    private final String householdTypeName;
    private final BigDecimal area;
    private final BigDecimal supplyArea;
    private final BigDecimal exclusiveArea;
    private final String description;
    private final LocalDateTime createdAt;
}
