package com.apten.household.application.model.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HouseholdTypeRes {

    private Long typeId;

    private String typeCode;

    private String typeName;

    private BigDecimal exclusiveAreaM2;

    private String description;

    private Boolean isActive;
}
