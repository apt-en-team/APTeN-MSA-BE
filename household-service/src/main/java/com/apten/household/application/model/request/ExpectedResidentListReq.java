package com.apten.household.application.model.request;

import com.apten.household.domain.enums.ExpectedResidentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자 명부 목록 조회 요청 DTO이다.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpectedResidentListReq {

    // 명부 상태 조건이다.
    private Long householdId;

    private ExpectedResidentStatus status;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
