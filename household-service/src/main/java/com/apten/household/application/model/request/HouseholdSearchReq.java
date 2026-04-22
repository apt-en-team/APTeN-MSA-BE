package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 목록 검색 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdSearchReq {
    private String apartmentComplexUid;
    private String status;
    private String building;
    private String unit;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;
}
