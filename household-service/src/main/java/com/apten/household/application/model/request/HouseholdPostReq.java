package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 마스터 등록 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdPostReq {
    private String apartmentComplexUid;
    private String building;
    private String unit;
    private String householdTypeUid;
}
