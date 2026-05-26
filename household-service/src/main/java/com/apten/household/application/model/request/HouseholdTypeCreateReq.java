package com.apten.household.application.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdTypeCreateReq {

    private String typeCode;

    private String typeName;

    private BigDecimal exclusiveAreaM2;

    private String description;
}
