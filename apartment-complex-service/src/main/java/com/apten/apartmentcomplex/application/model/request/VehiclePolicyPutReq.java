package com.apten.apartmentcomplex.application.model.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePolicyPutReq {

    // 차량 대수별 요금 정책 목록
    private List<VehiclePolicyItem> policies;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclePolicyItem {
        private Integer carCount;
        private BigDecimal monthlyFee;
    }
}
