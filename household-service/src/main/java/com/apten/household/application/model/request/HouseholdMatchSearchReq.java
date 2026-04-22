package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 수동 승인 대상 목록 검색 요청
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMatchSearchReq {
    private String status;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;
}
