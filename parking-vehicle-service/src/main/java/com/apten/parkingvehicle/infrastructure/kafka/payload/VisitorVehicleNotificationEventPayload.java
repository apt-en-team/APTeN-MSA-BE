package com.apten.parkingvehicle.infrastructure.kafka.payload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문차량 알림 이벤트 payload이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorVehicleNotificationEventPayload {

    // 방문차량 ID이다.
    private Long visitorVehicleId;

    // 단지 ID이다.
    private Long complexId;

    // 세대 ID이다.
    private Long householdId;

    // 차량 번호이다.
    private String licensePlate;

    // 방문자 이름이다.
    private String visitorName;

    // 방문 예정일이다.
    private LocalDate visitDate;

    // 행위 주체 사용자 ID이다.
    private Long actorUserId;

    // 알림 대상 구분 문자열이다.
    private String notificationTarget;

    // 발생 시각이다.
    private LocalDateTime occurredAt;
}
