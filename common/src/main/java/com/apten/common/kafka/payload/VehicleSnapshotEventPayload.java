package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// household-service의 vehicle_snapshot 동기화를 위한 공통 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSnapshotEventPayload {

    // 원본 차량 식별자이다.
    private Long vehicleId;

    // 세대 식별자이다.
    private Long householdId;

    // 단지 식별자이다.
    private Long complexId;

    // 차량 번호이다.
    private String licensePlate;

    // 차량 상태이다.
    private String status;

    // 삭제 여부이다.
    private Boolean isDeleted;
}
