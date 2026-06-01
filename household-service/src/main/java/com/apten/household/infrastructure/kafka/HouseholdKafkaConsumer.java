package com.apten.household.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdMatchRequestEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.household.application.model.request.FacilityFeeReflectReq;
import com.apten.household.application.model.request.VehicleFeeReflectReq;
import com.apten.household.application.model.request.VisitorFeeReflectReq;
import com.apten.household.application.service.HouseholdBillService;
import com.apten.household.application.service.HouseholdMatchService;
import com.apten.household.application.service.HouseholdReferenceCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HouseholdKafkaConsumer {

    private static final String VEHICLE_FEE_CALCULATED_TOPIC = "vehicle.fee.calculated";
    private static final String FACILITY_FEE_CALCULATED_TOPIC = "facility.fee.calculated";
    private static final String VISITOR_FEE_CALCULATED_TOPIC = "visitor.fee.calculated";

    private final HouseholdReferenceCacheService householdReferenceCacheService;
    private final HouseholdMatchService householdMatchService;
    private final HouseholdBillService householdBillService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.USER, groupId = "household-service-user-cache")
    public void consumeUserEvent(String message) {
        try {
            EventEnvelope<UserEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<UserEventPayload>>() {}
                    );

            log.info(
                    "Consumed user event. eventType={}, eventId={}",
                    eventEnvelope.getEventType(),
                    eventEnvelope.getEventId()
            );

            householdReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume user event. message={}", message, exception);
        }
    }

    @KafkaListener(topics = KafkaTopics.APARTMENT_COMPLEX, groupId = "household-service-complex-cache")
    public void consumeApartmentComplexEvent(String message) {
        try {
            EventEnvelope<ApartmentComplexEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<ApartmentComplexEventPayload>>() {}
                    );

            log.info(
                    "Consumed apartment complex event. eventType={}, eventId={}",
                    eventEnvelope.getEventType(),
                    eventEnvelope.getEventId()
            );

            householdReferenceCacheService.upsertComplexCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume apartment complex event. message={}", message, exception);
        }
    }

    @KafkaListener(topics = KafkaTopics.HOUSEHOLD, groupId = "household-service-match-request")
    public void consumeHouseholdMatchRequestedEvent(String message) {
        try {
            EventEnvelope<HouseholdMatchRequestEventPayload> eventEnvelope =
                    objectMapper.readValue(
                            message,
                            new TypeReference<EventEnvelope<HouseholdMatchRequestEventPayload>>() {}
                    );

            if (eventEnvelope.getEventType() != EventType.HOUSEHOLD_MATCH_REQUESTED) {
                return;
            }

            log.info(
                    "Consumed household match request event. eventType={}, eventId={}",
                    eventEnvelope.getEventType(),
                    eventEnvelope.getEventId()
            );

            householdMatchService.createMatchRequest(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume household match request event. message={}", message, exception);
        }
    }

    @KafkaListener(topics = VEHICLE_FEE_CALCULATED_TOPIC, groupId = "household-service-vehicle-fee")
    public void consumeVehicleFeeCalculatedEvent(String message) {
        try {
            JsonNode payload = payloadNode(message);
            List<VehicleFeeReflectReq.Item> items = new ArrayList<>();
            for (JsonNode item : payload.path("items")) {
                items.add(VehicleFeeReflectReq.Item.builder()
                        .householdId(longValue(item, "householdId"))
                        .vehicleFee(decimalValue(item, "vehicleFee"))
                        .build());
            }

            householdBillService.reflectVehicleFee(VehicleFeeReflectReq.builder()
                    .complexId(longValue(payload, "complexId"))
                    .billYear(intValue(payload, "billYear"))
                    .billMonth(intValue(payload, "billMonth"))
                    .items(items)
                    .build());

            log.info("Consumed vehicle fee calculated event. complexId={}, billYear={}, billMonth={}, itemCount={}",
                    longValue(payload, "complexId"), intValue(payload, "billYear"), intValue(payload, "billMonth"), items.size());
        } catch (Exception exception) {
            log.error("Failed to consume vehicle fee calculated event. message={}", message, exception);
        }
    }

    @KafkaListener(topics = FACILITY_FEE_CALCULATED_TOPIC, groupId = "household-service-facility-fee")
    public void consumeFacilityFeeCalculatedEvent(String message) {
        try {
            JsonNode payload = payloadNode(message);
            List<FacilityFeeReflectReq.Item> items = new ArrayList<>();
            for (JsonNode item : payload.path("items")) {
                items.add(FacilityFeeReflectReq.Item.builder()
                        .householdId(longValue(item, "householdId"))
                        .facilityFee(decimalValue(item, "facilityFee"))
                        .build());
            }

            householdBillService.reflectFacilityFee(FacilityFeeReflectReq.builder()
                    .complexId(longValue(payload, "complexId"))
                    .billYear(intValue(payload, "usageYear"))
                    .billMonth(intValue(payload, "usageMonth"))
                    .items(items)
                    .build());

            log.info("Consumed facility fee calculated event. complexId={}, usageYear={}, usageMonth={}, itemCount={}",
                    longValue(payload, "complexId"), intValue(payload, "usageYear"), intValue(payload, "usageMonth"), items.size());
        } catch (Exception exception) {
            log.error("Failed to consume facility fee calculated event. message={}", message, exception);
        }
    }

    @KafkaListener(topics = VISITOR_FEE_CALCULATED_TOPIC, groupId = "household-service-visitor-fee")
    public void consumeVisitorFeeCalculatedEvent(String message) {
        try {
            JsonNode payload = payloadNode(message);
            List<VisitorFeeReflectReq.Item> items = new ArrayList<>();
            for (JsonNode item : payload.path("items")) {
                items.add(VisitorFeeReflectReq.Item.builder()
                        .householdId(longValue(item, "householdId"))
                        .totalMinutes(intValue(item, "totalMinutes"))
                        .totalHours(decimalValue(item, "totalHours"))
                        .freeMinutes(intValue(item, "freeMinutes"))
                        .extraMinutes(intValue(item, "extraMinutes"))
                        .visitorFee(decimalValue(item, "visitorFee"))
                        .build());
            }

            householdBillService.reflectVisitorFee(VisitorFeeReflectReq.builder()
                    .complexId(longValue(payload, "complexId"))
                    .billYear(intValue(payload, "billYear"))
                    .billMonth(intValue(payload, "billMonth"))
                    .items(items)
                    .build());

            log.info("Consumed visitor fee calculated event. complexId={}, billYear={}, billMonth={}, itemCount={}",
                    longValue(payload, "complexId"), intValue(payload, "billYear"), intValue(payload, "billMonth"), items.size());
        } catch (Exception exception) {
            log.error("Failed to consume visitor fee calculated event. message={}", message, exception);
        }
    }

    private JsonNode payloadNode(String message) throws Exception {
        JsonNode root = objectMapper.readTree(message);
        JsonNode payload = root.path("payload");
        return payload.isMissingNode() || payload.isNull() ? root : payload;
    }

    private Long longValue(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asLong();
    }

    private Integer intValue(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }

    private BigDecimal decimalValue(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : new BigDecimal(value.asText());
    }
}
