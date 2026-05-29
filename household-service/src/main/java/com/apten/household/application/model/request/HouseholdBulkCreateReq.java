package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdBulkCreateReq {

    private String building;

    private Integer floorStart;

    private Integer floorEnd;

    private Integer lineStart;

    private Integer lineEnd;

    private Long typeId;
}
