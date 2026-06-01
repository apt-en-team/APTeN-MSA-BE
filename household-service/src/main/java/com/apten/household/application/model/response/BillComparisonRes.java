package com.apten.household.application.model.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillComparisonRes {

    // 동일 평형 전용면적 (m²)이다.
    private BigDecimal areaSize;

    // 현재 세대의 최근 6개월 청구 금액이다.
    private List<BigDecimal> myAmounts;

    // 현재 세대의 6개월 평균 청구 금액이다.
    private BigDecimal myAverage;

    // 동일 평형 세대들의 최근 6개월 월별 평균 청구 금액이다.
    private List<BigDecimal> avgAmounts;

    // 동일 평형 세대들의 6개월 전체 평균 청구 금액이다.
    private BigDecimal sameTypeAverage;

    // 현재 월 기준 동일 평형 내 우리집 순위이다.
    private Integer rank;

    // 현재 월 기준 동일 평형 전체 세대 수이다.
    private Integer totalHouseholds;
}