package com.apten.parkingvehicle.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 정책 조회 응답 DTO이다.
@Getter
@Builder
public class VehiclePolicyListRes {

    // 단지 ID이다.
    private Long complexId;

    // 차량 정책 목록이다.
    private List<Item> policies;

    // 마지막 수정 시각이다.
    private LocalDateTime updatedAt;

    // 차량 대수별 정책 항목이다.
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        // 차량 대수이다.
        private Integer carCount;

        // 월 요금이다.
        private BigDecimal monthlyFee;

        // 제한 정책 사용 여부이다.
        private Boolean isLimitRule;

        // 활성 여부이다.
        private Boolean isActive;
    }
}
