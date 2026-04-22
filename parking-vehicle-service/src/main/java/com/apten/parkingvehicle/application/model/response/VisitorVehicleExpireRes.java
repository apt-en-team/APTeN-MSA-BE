package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 자동 만료 실행 결과 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehicleExpireRes {
    private Integer expiredCount;
    private LocalDateTime executedAt;
}
