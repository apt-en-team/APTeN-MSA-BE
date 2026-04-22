package com.apten.apartmentcomplex.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 유형 등록 요청 DTO
// 단지별 평형 유형 정보를 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdTypePostReq {
    private String householdTypeName;
    private BigDecimal area;
    private BigDecimal supplyArea;
    private BigDecimal exclusiveArea;
    private String description;
}
