package com.apten.household.application.model.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuildingLineTypeRes {

    private Long lineTypeId;

    private Long complexId;

    private String building;

    private Integer lineStart;

    private Integer lineEnd;

    private Long typeId;

    private String typeCode;

    private String typeName;

    private BigDecimal exclusiveAreaM2;

    private Boolean isActive;
}
