package com.apten.common.kafka.payload;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 자리 점유 변경 이벤트의 Kafka payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotChangedEventPayload {

    // 단지 ID이다.
    private Long complexId;

    // 센서 식별 코드이다.
    private String sensorCode;

    // 자리 번호 표시 값이다.
    private String spotNumber;

    // 구역 ID이다.
    private Long zoneId;

    // 자리 점유 상태 코드이다.
    private String status;

    // 해당 zone 현재 점유 수이다.
    private Integer zoneOccupied;

    // 해당 zone 전체 면수이다.
    private Integer zoneTotalSlots;

    // 변경 시각이다.
    private Instant changedAt;
}
