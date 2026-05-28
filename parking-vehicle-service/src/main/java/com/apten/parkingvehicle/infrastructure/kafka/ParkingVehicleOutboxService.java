package com.apten.parkingvehicle.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.payload.ParkingSpotChangedEventPayload;
import com.apten.common.outbox.Outbox;
import com.apten.common.outbox.OutboxRepository;
import com.apten.parkingvehicle.application.model.event.ParkingSpotChangedEvent;
import com.apten.parkingvehicle.domain.entity.RegularVisitorVehicle;
import com.apten.parkingvehicle.domain.entity.Vehicle;
import com.apten.parkingvehicle.domain.entity.VisitorVehicle;
import com.apten.parkingvehicle.infrastructure.kafka.payload.RegularVisitorVehicleNotificationEventPayload;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VehicleFeeCalculatedEventPayload;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VehicleStatusChangedEventPayload;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VisitorFeeCalculatedEventPayload;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VisitorVehicleNotificationEventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// parking-vehicle-service의 outbox 적재를 담당하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
public class ParkingVehicleOutboxService {

    // 차량 상태 변경 토픽이다.
    private static final String VEHICLE_STATUS_CHANGED_TOPIC = "vehicle.status.changed";

    // 차량 비용 산정 토픽이다.
    private static final String VEHICLE_FEE_CALCULATED_TOPIC = "vehicle.fee.calculated";

    // 방문차량 비용 산정 토픽이다.
    private static final String VISITOR_FEE_CALCULATED_TOPIC = "visitor.fee.calculated";

    // 자리 점유 변경 토픽이다.
    private static final String PARKING_SPOT_CHANGED_TOPIC = "parking.spot.changed";

    // 방문차량 알림 토픽이다.
    private static final String VISITOR_VEHICLE_NOTIFICATION_TOPIC = "visitor-vehicle.notification";

    // 고정 방문차량 알림 토픽이다.
    private static final String REGULAR_VISITOR_VEHICLE_NOTIFICATION_TOPIC = "regular-visitor-vehicle.notification";

    // 방문차량 알림 action 문자열이다.
    public static final String VISITOR_VEHICLE_REGISTERED = "VISITOR_VEHICLE_REGISTERED";
    public static final String VISITOR_VEHICLE_UPDATED = "VISITOR_VEHICLE_UPDATED";
    public static final String VISITOR_VEHICLE_CANCELLED = "VISITOR_VEHICLE_CANCELLED";
    public static final String VISITOR_VEHICLE_RE_REGISTERED = "VISITOR_VEHICLE_RE_REGISTERED";
    public static final String VISITOR_VEHICLE_CREATED_BY_ADMIN = "VISITOR_VEHICLE_CREATED_BY_ADMIN";

    // 고정 방문차량 알림 action 문자열이다.
    public static final String REGULAR_VISITOR_VEHICLE_REGISTERED = "REGULAR_VISITOR_VEHICLE_REGISTERED";
    public static final String REGULAR_VISITOR_VEHICLE_UPDATED = "REGULAR_VISITOR_VEHICLE_UPDATED";
    public static final String REGULAR_VISITOR_VEHICLE_FORCE_DELETED_BY_ADMIN = "REGULAR_VISITOR_VEHICLE_FORCE_DELETED_BY_ADMIN";

    // 알림 대상 구분 문자열이다.
    public static final String TARGET_ADMIN = "ADMIN";
    public static final String TARGET_HOUSEHOLD = "HOUSEHOLD";

    // outbox 저장소이다.
    private final OutboxRepository outboxRepository;

    // payload 직렬화 도구이다.
    private final ObjectMapper objectMapper;

    // 차량 상태 변경 이벤트를 outbox에 적재한다.
    public void saveVehicleStatusChangedEvent(Vehicle vehicle) {
        //TODO 승인, 거절, 삭제 시점에 호출하도록 서비스 로직과 연결
        VehicleStatusChangedEventPayload payload = VehicleStatusChangedEventPayload.builder()
                .vehicleId(vehicle.getId())
                .complexId(vehicle.getComplexId())
                .householdId(vehicle.getHouseholdId())
                .userId(vehicle.getUserId())
                .licensePlate(vehicle.getLicensePlate())
                .status(vehicle.getStatus().name())
                .isDeleted(vehicle.getIsDeleted())
                .occurredAt(java.time.LocalDateTime.now())
                .build();
        saveOutboxEvent(VEHICLE_STATUS_CHANGED_TOPIC, "VEHICLE_STATUS_CHANGED", vehicle.getId(), payload);
    }

    // 차량 비용 산정 이벤트를 outbox에 적재한다.
    public void saveVehicleFeeCalculatedEvent(Long aggregateId, VehicleFeeCalculatedEventPayload payload) {
        //TODO 월 차량 비용 산정 완료 시점에 호출하도록 서비스 로직과 연결
        saveOutboxEvent(VEHICLE_FEE_CALCULATED_TOPIC, "VEHICLE_FEE_CALCULATED", aggregateId, payload);
    }

    // 방문차량 비용 산정 이벤트를 outbox에 적재한다.
    public void saveVisitorFeeCalculatedEvent(Long aggregateId, VisitorFeeCalculatedEventPayload payload) {
        //TODO 월 방문차량 비용 산정 완료 시점에 호출하도록 서비스 로직과 연결
        saveOutboxEvent(VISITOR_FEE_CALCULATED_TOPIC, "VISITOR_FEE_CALCULATED", aggregateId, payload);
    }

    // 자리 점유 변경 이벤트를 envelope로 감싸 outbox에 적재한다.
    public void saveParkingSpotChangedEvent(ParkingSpotChangedEvent event) {
        ParkingSpotChangedEventPayload payload = ParkingSpotChangedEventPayload.builder()
                .complexId(event.getComplexId())
                .sensorCode(event.getSensorCode())
                .spotNumber(event.getSpotNumber())
                .zoneId(event.getZoneId())
                .status(event.getStatus().name())
                .zoneOccupied(event.getZoneOccupied())
                .zoneTotalSlots(event.getZoneTotalSlots())
                .changedAt(event.getChangedAt().atZone(ZoneId.systemDefault()).toInstant())
                .build();

        EventEnvelope<ParkingSpotChangedEventPayload> envelope = EventEnvelope.<ParkingSpotChangedEventPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(EventType.PARKING_SPOT_CHANGED)
                .version(1)
                .occurredAt(Instant.now())
                .producer("parking-vehicle-service")
                .payload(payload)
                .build();

        saveOutboxEvent(PARKING_SPOT_CHANGED_TOPIC, EventType.PARKING_SPOT_CHANGED.name(), event.getZoneId(), envelope);
    }

    // 방문차량 알림 이벤트를 outbox에 적재한다.
    // 토픽 visitor-vehicle.notification은 notification-service가 구독 예정이며, 세대/관리자 팬아웃은 소비처 책임이다.
    public void saveVisitorVehicleNotificationEvent(
            VisitorVehicle entity, String action, String notificationTarget, Long actorUserId) {
        VisitorVehicleNotificationEventPayload payload = VisitorVehicleNotificationEventPayload.builder()
                .visitorVehicleId(entity.getId())
                .complexId(entity.getComplexId())
                .householdId(entity.getHouseholdId())
                .licensePlate(entity.getLicensePlate())
                .visitorName(entity.getVisitorName())
                .visitDate(entity.getVisitDate())
                .actorUserId(actorUserId)
                .notificationTarget(notificationTarget)
                .occurredAt(LocalDateTime.now())
                .build();
        saveOutboxEvent(VISITOR_VEHICLE_NOTIFICATION_TOPIC, action, entity.getId(), payload);
    }

    // 고정 방문차량 알림 이벤트를 outbox에 적재한다.
    // 토픽 regular-visitor-vehicle.notification은 notification-service가 구독 예정이며, 세대/관리자 팬아웃은 소비처 책임이다.
    public void saveRegularVisitorVehicleNotificationEvent(
            RegularVisitorVehicle entity, String action, String notificationTarget, Long actorUserId) {
        RegularVisitorVehicleNotificationEventPayload payload = RegularVisitorVehicleNotificationEventPayload.builder()
                .regularVisitorVehicleId(entity.getId())
                .complexId(entity.getComplexId())
                .householdId(entity.getHouseholdId())
                .licensePlate(entity.getLicensePlate())
                .visitorName(entity.getVisitorName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .actorUserId(actorUserId)
                .notificationTarget(notificationTarget)
                .occurredAt(LocalDateTime.now())
                .build();
        saveOutboxEvent(REGULAR_VISITOR_VEHICLE_NOTIFICATION_TOPIC, action, entity.getId(), payload);
    }

    // payload를 JSON으로 저장하고 outbox row를 생성한다.
    private void saveOutboxEvent(String topic, String eventType, Long aggregateId, Object payload) {
        try {
            Outbox outbox = Outbox.builder()
                    .topic(topic)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(payload))
                    .build();
            outboxRepository.save(outbox);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Outbox payload 직렬화에 실패했습니다.", exception);
        }
    }
}
