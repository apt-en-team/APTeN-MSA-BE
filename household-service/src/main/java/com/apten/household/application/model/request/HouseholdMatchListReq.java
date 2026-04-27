package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdMatchProcessType;
import com.apten.household.domain.enums.HouseholdMatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 수동 승인 대상 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMatchListReq {

    // 단지 ID 조건이다.
    private Long complexId;

    // 매칭 상태 조건이다.
    private HouseholdMatchStatus matchStatus;

    // 처리 방식 조건이다.
    private HouseholdMatchProcessType processType;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
