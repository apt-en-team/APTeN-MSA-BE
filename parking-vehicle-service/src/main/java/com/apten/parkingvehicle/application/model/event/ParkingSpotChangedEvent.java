
package com.apten.parkingvehicle.application.model.event;

import com.apten.parkingvehicle.domain.enums.SensorStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// SSE로 발행하는 자리 단위 점유 상태 변경 이벤트이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotChangedEvent {

    // 단지 ID이다.
    private Long complexId;

    // 센서 식별 코드이다.
    private String sensorCode;

    // 자리 번호 표시 값이다.
    private String spotNumber;

    // 구역 ID이다.
    private Long zoneId;

    // 자리 점유 상태이다.
    private SensorStatus status;

    // 자리 활성 여부이다. false면 사용불가.
    private Boolean isActive;

    // 해당 zone 현재 점유 수이다.
    private int zoneOccupied;

    // 해당 zone 전체 면수이다.
    private int zoneTotalSlots;

    // 변경 시각이다.
    private LocalDateTime changedAt;
}
