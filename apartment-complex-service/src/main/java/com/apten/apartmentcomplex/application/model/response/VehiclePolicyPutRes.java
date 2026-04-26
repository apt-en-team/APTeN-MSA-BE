package com.apten.apartmentcomplex.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

// 차량 정책 설정 응답 DTO
@Getter
@Builder
public class VehiclePolicyPutRes {

    private final String apartmentComplexUid;
    private final List<VehiclePolicyItem> policies;
    private final LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class VehiclePolicyItem {
        private final Integer carCount;
        private final BigDecimal monthlyFee;
    }
}
