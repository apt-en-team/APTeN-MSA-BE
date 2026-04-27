package com.apten.parkingvehicle.infrastructure.kafka.payload;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 상태 변경 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleStatusChangedEventPayload {

    // 차량 ID이다.
    private Long vehicleId;

    // 단지 ID이다.
    private Long complexId;

    // 세대 ID이다.
    private Long householdId;

    // 사용자 ID이다.
    private Long userId;

    // 차량 번호이다.
    private String licensePlate;

    // 상태 문자열이다.
    private String status;

    // 삭제 여부이다.
    private Boolean isDeleted;

    // 발생 시각이다.
    private LocalDateTime occurredAt;
}
