package com.apten.household.application.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HouseholdBulkCreateRes {

    private Long complexId;

    private String building;

    private Integer floorStart;

    private Integer floorEnd;

    private Integer lineStart;

    private Integer lineEnd;

    private Long typeId;

    private int createdCount;

    private int skippedCount;

    private List<String> createdUnits;

    private List<String> skippedUnits;
}
