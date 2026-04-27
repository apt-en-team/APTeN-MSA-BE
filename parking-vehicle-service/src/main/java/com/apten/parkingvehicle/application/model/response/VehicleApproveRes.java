package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 승인 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleApproveRes {

    // 차량 ID이다.
    private Long vehicleId;

    // 처리 상태이다.
    private VehicleStatus status;

    // 승인 시각이다.
    private LocalDateTime approvedAt;
}
