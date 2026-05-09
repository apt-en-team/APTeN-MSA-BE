package com.apten.parkingvehicle.application.model.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 정책 설정 요청 DTO이다.
// 차량 대수별 월 요금 정책 목록을 받을 때 사용한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePolicyPutReq {

    // 차량 대수별 요금 정책 목록이다.
    private List<VehiclePolicyItem> policies;

    // 목록 안의 각 차량 대수 정책 항목이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclePolicyItem {

        // 차량 대수이다.
        private Integer carCount;

        // 해당 차량 대수에 적용할 월 요금이다.
        private BigDecimal monthlyFee;
    }
}
