package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 거절 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRejectRes {

    // 차량 ID이다.
    private Long vehicleId;

    // 처리 상태이다.
    private VehicleStatus status;

    // 거절 사유이다.
    private String rejectReason;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
