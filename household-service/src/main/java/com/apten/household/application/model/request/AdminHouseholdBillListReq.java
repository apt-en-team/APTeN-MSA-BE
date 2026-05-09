package com.apten.household.application.model.request;

import com.apten.household.domain.enums.HouseholdBillStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자 관리비 목록 조회 요청 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminHouseholdBillListReq {

    // 청구 연도이다.
    private Integer billYear;

    // 청구 월이다.
    private Integer billMonth;

    // 청구 상태이다.
    private HouseholdBillStatus status;

    // 동 정보이다.
    private String building;

    // 호 정보이다.
    private String unit;

    // 페이지 번호이다.
    @Builder.Default
    private Integer page = 0;

    // 페이지 크기이다.
    @Builder.Default
    private Integer size = 20;
}
