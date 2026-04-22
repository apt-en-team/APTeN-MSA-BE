package com.apten.apartmentcomplex.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 유형 수정 요청 DTO
// 기존 세대 유형 값을 변경할 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdTypePatchReq {
    private String householdTypeName;
    private BigDecimal area;
    private BigDecimal supplyArea;
    private BigDecimal exclusiveArea;
    private String description;
}
