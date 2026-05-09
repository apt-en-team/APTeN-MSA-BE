package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdListReq {

    // 단지 ID 조건이다.
    private Long complexId;

    // 동 조건이다.
    private String building;

    // 호 조건이다.
    private String unit;

    // 세대 상태 조건이다.
    private HouseholdStatus status;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
