package com.apten.parkingvehicle.application.model.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// SSE로 발행하는 zone 단위 점유 카운터 변경 이벤트이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneCounterChangedEvent {

    // 단지 ID이다.
    private Long complexId;

    // 구역 ID이다.
    private Long zoneId;

    // 변경 후 현재 점유 수이다.
    private int zoneOccupied;

    // 구역 전체 자리 수이다.
    private int zoneTotalSlots;

    // 변경 시각이다.
    private LocalDateTime changedAt;
}
