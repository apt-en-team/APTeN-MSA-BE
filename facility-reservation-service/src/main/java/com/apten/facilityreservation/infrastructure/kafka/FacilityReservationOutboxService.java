package com.apten.facilityreservation.infrastructure.kafka;

import com.apten.common.outbox.Outbox;
import com.apten.common.outbox.OutboxRepository;
import com.apten.facilityreservation.infrastructure.kafka.payload.FacilityFeeCalculatedEventPayload;
import com.apten.facilityreservation.infrastructure.kafka.payload.GxProgramCancelledEventPayload;
import com.apten.facilityreservation.infrastructure.kafka.payload.ReservationStatusChangedEventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// facility-reservation-service의 outbox 적재 골격을 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
public class FacilityReservationOutboxService {

    // reservation 상태 변경 토픽명이다.
    private static final String RESERVATION_STATUS_CHANGED_TOPIC = "reservation.status.changed";

    // gx 프로그램 취소 토픽명이다.
    private static final String GX_PROGRAM_CANCELLED_TOPIC = "gx.program.cancelled";

    // 시설 비용 산정 토픽명이다.
    private static final String FACILITY_FEE_CALCULATED_TOPIC = "facility.fee.calculated";

    // outbox 저장소이다.
    private final OutboxRepository outboxRepository;

    // JSON 직렬화에 사용한다.
    private final ObjectMapper objectMapper;

    // 예약 상태 변경 이벤트를 outbox에 저장한다.
    public void saveReservationStatusChangedEvent(ReservationStatusChangedEventPayload payload) {
        //TODO common EventType이 준비되면 EventEnvelope 구조로 감싸서 저장
        saveOutboxPayload(RESERVATION_STATUS_CHANGED_TOPIC, payload.getReservationId(), RESERVATION_STATUS_CHANGED_TOPIC, payload);
    }

    // GX 프로그램 취소 이벤트를 outbox에 저장한다.
    public void saveGxProgramCancelledEvent(GxProgramCancelledEventPayload payload) {
        //TODO common EventType이 준비되면 EventEnvelope 구조로 감싸서 저장
        saveOutboxPayload(GX_PROGRAM_CANCELLED_TOPIC, payload.getProgramId(), GX_PROGRAM_CANCELLED_TOPIC, payload);
    }

    // 시설 이용 비용 산정 이벤트를 outbox에 저장한다.
    public void saveFacilityFeeCalculatedEvent(FacilityFeeCalculatedEventPayload payload) {
        //TODO Household Service 연동 topic 계약이 확정되면 공통 이벤트 구조로 정리
        saveOutboxPayload(FACILITY_FEE_CALCULATED_TOPIC, payload.getComplexId(), FACILITY_FEE_CALCULATED_TOPIC, payload);
    }

    // 공통 outbox row 저장을 처리한다.
    private void saveOutboxPayload(String topic, Long aggregateId, String eventType, Object payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            Outbox outbox = Outbox.builder()
                    .topic(topic)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payloadJson)
                    .build();
            outboxRepository.save(outbox);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("시설 예약 이벤트 직렬화에 실패했습니다.", exception);
        }
    }
}
