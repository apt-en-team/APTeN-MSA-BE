package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingLineTypeCreateReq {

    private String building;

    private Integer lineStart;

    private Integer lineEnd;

    private Long typeId;
}
