package com.apten.apartmentcomplex.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 차량 정책 설정 응답 DTO
// 단지 차량 정책 저장 결과를 내려줄 때 사용한다
@Getter
@Builder
public class VehiclePolicyPutRes {
    private final String apartmentComplexUid;
    private final Integer maxVehicleCountPerHousehold;
    private final Integer freeVehicleCount;
    private final BigDecimal extraVehicleFee;
    private final Integer visitorFreeMinutes;
    private final LocalDateTime updatedAt;
}
