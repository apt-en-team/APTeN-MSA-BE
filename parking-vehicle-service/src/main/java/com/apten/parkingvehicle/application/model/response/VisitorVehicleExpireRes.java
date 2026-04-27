package com.apten.parkingvehicle.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 자동 만료 실행 결과 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehicleExpireRes {

    // 만료 처리 건수이다.
    private Integer expiredCount;

    // 실행 시각이다.
    private LocalDateTime executedAt;
}
