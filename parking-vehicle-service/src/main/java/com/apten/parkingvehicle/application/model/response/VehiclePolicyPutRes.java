package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 차량 정책 설정 응답 DTO이다.
// 저장된 차량 대수별 월 요금 정책 목록을 내려줄 때 사용한다.
@Getter
@Builder
public class VehiclePolicyPutRes {

    // 단지 ID이다.
    private final Long complexId;

    // 차량 대수별 요금 정책 목록이다.
    private final List<VehiclePolicyItem> policies;

    // 정책 저장이 끝난 시각이다.
    private final LocalDateTime updatedAt;

    // 응답 안의 각 차량 대수 정책 항목이다.
    @Getter
    @Builder
    public static class VehiclePolicyItem {

        // 차량 대수이다.
        private final Integer carCount;

        // 해당 차량 대수에 적용된 월 요금이다.
        private final BigDecimal monthlyFee;
    }
}
