package com.apten.household.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 마스터 등록 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdCreateReq {

    // 세대가 속한 단지 ID이다.
    private Long complexId;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 세대 유형 ID이다.
    private Long typeId;
}
