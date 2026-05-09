package com.apten.parkingvehicle.application.model.response;

import com.apten.parkingvehicle.domain.enums.VisitorVehicleStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 취소 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehicleCancelRes {

    // 방문차량 ID이다.
    private Long visitorVehicleId;

    // 처리 상태이다.
    private VisitorVehicleStatus status;

    // 수정 시각이다.
    private LocalDateTime updatedAt;
}
