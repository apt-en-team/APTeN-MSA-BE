package com.apten.parkingvehicle.infrastructure.kafka;

import com.apten.common.outbox.Outbox;
import com.apten.common.outbox.OutboxRepository;
import com.apten.parkingvehicle.domain.entity.Vehicle;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VehicleFeeCalculatedEventPayload;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VehicleStatusChangedEventPayload;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VisitorFeeCalculatedEventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 주차 차량 서비스 전용 outbox 적재를 담당하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class ParkingVehicleOutboxService {

    // 차량 상태 변경 토픽이다.
    private static final String VEHICLE_STATUS_CHANGED_TOPIC = "vehicle.status.changed";

    // 차량 비용 산정 토픽이다.
    private static final String VEHICLE_FEE_CALCULATED_TOPIC = "vehicle.fee.calculated";

    // 방문차량 비용 산정 토픽이다.
    private static final String VISITOR_FEE_CALCULATED_TOPIC = "visitor.fee.calculated";

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
