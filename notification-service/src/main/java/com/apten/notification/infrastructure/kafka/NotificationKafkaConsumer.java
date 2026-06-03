package com.apten.notification.infrastructure.kafka;

import com.apten.common.kafka.EventEnvelope;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.KafkaTopics;
import com.fasterxml.jackson.databind.JsonNode;
import com.apten.common.kafka.payload.NotificationAdminBroadcastEventPayload;
import com.apten.common.kafka.payload.NotificationEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.notification.application.model.request.NotificationAdminBroadcastPostReq;
import com.apten.notification.application.model.request.NotificationPostReq;
import com.apten.notification.application.service.NotificationReferenceCacheService;
import com.apten.notification.application.service.NotificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// notification-service Kafka consumer: user_cache 동기화 + 시설예약 알림 이벤트 처리
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationReferenceCacheService notificationReferenceCacheService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    // 사용자 이벤트를 받아 user_cache를 최신 상태로 맞춘다.
    @KafkaListener(topics = KafkaTopics.USER, groupId = "notification-service-user-cache")
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

            notificationReferenceCacheService.upsertUserCache(eventEnvelope.getPayload());
        } catch (Exception exception) {
            log.error("Failed to consume user event. message={}", message, exception);
        }
    }

    // 알림 생성 요청 이벤트를 받아 알림을 생성한다 (전 서비스 공용 토픽).
    @KafkaListener(topics = KafkaTopics.NOTIFICATION_REQUEST, groupId = "notification-service-request")
    public void consumeNotificationRequestEvent(String message) {
        try {
            EventEnvelope<JsonNode> envelope = objectMapper.readValue(
                    message,
                    new TypeReference<EventEnvelope<JsonNode>>() {}
            );

            String eventType = envelope.getEventType() == null ? null : envelope.getEventType().name();
            log.info("Consumed notification request event. eventType={}, eventId={}",
                    eventType, envelope.getEventId());

            if (EventType.NOTIFICATION_REQUESTED.name().equals(eventType)) {
                NotificationEventPayload payload = objectMapper.treeToValue(
                        envelope.getPayload(), NotificationEventPayload.class);
                notificationService.createNotification(toPostReq(payload));

            } else if (EventType.ADMIN_NOTIFICATION_REQUESTED.name().equals(eventType)) {
                NotificationAdminBroadcastEventPayload payload = objectMapper.treeToValue(
                        envelope.getPayload(), NotificationAdminBroadcastEventPayload.class);
                notificationService.createAdminBroadcastNotification(toBroadcastReq(payload));

            } else {
                log.warn("Unknown notification request eventType={}. message={}", eventType, message);
            }
        } catch (Exception exception) {
            log.error("Failed to consume notification request event. message={}", message, exception);
        }
    }

    private NotificationPostReq toPostReq(NotificationEventPayload payload) {
        return NotificationPostReq.builder()
                .receiverUserId(payload.getReceiverUserId())
                .complexId(payload.getComplexId())
                .type(payload.getType())
                .targetType(payload.getTargetType())
                .targetId(payload.getTargetId())
                .title(payload.getTitle())
                .content(payload.getContent())
                .linkPath(payload.getLinkPath())
                .payloadJson(payload.getPayloadJson())
                .build();
    }

    private NotificationAdminBroadcastPostReq toBroadcastReq(NotificationAdminBroadcastEventPayload payload) {
        return NotificationAdminBroadcastPostReq.builder()
                .complexId(payload.getComplexId())
                .type(payload.getType())
                .targetType(payload.getTargetType())
                .targetId(payload.getTargetId())
                .title(payload.getTitle())
                .content(payload.getContent())
                .linkPath(payload.getLinkPath())
                .payloadJson(payload.getPayloadJson())
                .build();
    }

    // 공지사항 등록 → 단지 입주민 전체 broadcast
    @KafkaListener(topics = KafkaTopics.BOARD_NOTICE_CREATED, groupId = "notification-service-notice")
    public void consumeNoticeCreatedEvent(String message) {
        try {
            JsonNode payload = payloadNode(message);
            Long complexId = longValue(payload, "complexId");
            Long noticeId  = longValue(payload, "noticeId");
            String title   = payload.path("title").asText();

            notificationService.createResidentBroadcastNotification(
                    complexId, "NOTICE_CREATED", "NOTICE", noticeId,
                    "새 공지사항이 등록되었습니다.",
                    title,
                    "/resident/" + complexId + "/notices/" + noticeId,
                    null
            );
        } catch (Exception e) {
            log.error("Failed to consume notice created event. message={}", message, e);
        }
    }

    // 게시글 등록 → 단지 입주민 전체 broadcast (작성자 본인 제외)
    @KafkaListener(topics = KafkaTopics.BOARD_POST_CREATED, groupId = "notification-service-post")
    public void consumePostCreatedEvent(String message) {
        try {
            JsonNode payload = payloadNode(message);
            Long complexId = longValue(payload, "complexId");
            Long postId    = longValue(payload, "postId");
            Long userId    = longValue(payload, "userId");
            String title   = payload.path("title").asText();

            notificationService.createResidentBroadcastNotification(
                    complexId, "POST_CREATED", "POST", postId,
                    "새 게시글이 등록되었습니다.",
                    title,
                    "/resident/" + complexId + "/board/" + postId,
                    userId
            );
        } catch (Exception e) {
            log.error("Failed to consume post created event. message={}", message, e);
        }
    }

    // 댓글 등록 → 게시글 작성자 단건 (본인 댓글 제외)
    @KafkaListener(topics = KafkaTopics.BOARD_COMMENT_CREATED, groupId = "notification-service-comment")
    public void consumeCommentCreatedEvent(String message) {
        try {
            JsonNode payload    = payloadNode(message);
            Long complexId      = longValue(payload, "complexId");
            Long postId         = longValue(payload, "postId");
            Long userId         = longValue(payload, "userId");
            Long postAuthorId   = longValue(payload, "postAuthorId");

            // 작성자 본인 댓글이면 알림 불필요
            if (postAuthorId == null || postAuthorId.equals(userId)) {
                return;
            }

            notificationService.createNotification(NotificationPostReq.builder()
                    .receiverUserId(postAuthorId)
                    .complexId(complexId)
                    .type("COMMENT_CREATED")
                    .targetType("COMMENT")
                    .targetId(longValue(payload, "commentId"))
                    .title("내 게시글에 댓글이 달렸습니다.")
                    .content(payload.path("content").asText())
                    .linkPath("/resident/" + complexId + "/board/" + postId)
                    .build());
        } catch (Exception e) {
            log.error("Failed to consume comment created event. message={}", message, e);
        }
    }

    // 투표 생성 → 단지 입주민 전체 broadcast
    @KafkaListener(topics = KafkaTopics.BOARD_VOTE_CREATED, groupId = "notification-service-vote-created")
    public void consumeVoteCreatedEvent(String message) {
        try {
            JsonNode payload = payloadNode(message);
            Long complexId   = longValue(payload, "complexId");
            Long voteId      = longValue(payload, "voteId");
            String title     = payload.path("title").asText();

            notificationService.createResidentBroadcastNotification(
                    complexId, "VOTE_CREATED", "VOTE", voteId,
                    "새 투표가 개설되었습니다.",
                    title,
                    "/resident/" + complexId + "/votes/" + voteId,
                    null
            );
        } catch (Exception e) {
            log.error("Failed to consume vote created event. message={}", message, e);
        }
    }

    // 투표 마감 → 단지 입주민 전체 broadcast
    @KafkaListener(topics = KafkaTopics.BOARD_VOTE_CLOSED, groupId = "notification-service-vote-closed")
    public void consumeVoteClosedEvent(String message) {
        try {
            JsonNode payload = payloadNode(message);
            Long complexId   = longValue(payload, "complexId");
            Long voteId      = longValue(payload, "voteId");
            String title     = payload.path("title").asText();

            notificationService.createResidentBroadcastNotification(
                    complexId, "VOTE_CLOSED", "VOTE", voteId,
                    "투표가 마감되었습니다.",
                    title,
                    "/resident/" + complexId + "/votes/" + voteId,
                    null
            );
        } catch (Exception e) {
            log.error("Failed to consume vote closed event. message={}", message, e);
        }
    }

    // 차량 상태 변경 이벤트 → VEHICLE_REGISTRATION_APPROVED / REJECTED 단건 알림
    @KafkaListener(topics = KafkaTopics.VEHICLE_STATUS_CHANGED, groupId = "notification-service-vehicle-status")
    public void consumeVehicleStatusChangedEvent(String message) {
        try {
            EventEnvelope<JsonNode> envelope = objectMapper.readValue(
                    message, new TypeReference<EventEnvelope<JsonNode>>() {}
            );
            JsonNode payload = envelope.getPayload();
            String status  = payload.path("status").asText();
            Long userId    = longValue(payload, "userId");
            Long complexId = longValue(payload, "complexId");
            Long vehicleId = longValue(payload, "vehicleId");
            String plate   = payload.path("licensePlate").asText();

            if (userId == null || complexId == null) return;

            if ("APPROVED".equals(status)) {
                notificationService.createNotification(NotificationPostReq.builder()
                        .receiverUserId(userId)
                        .complexId(complexId)
                        .type("VEHICLE_REGISTRATION_APPROVED")
                        .targetType("VEHICLE")
                        .targetId(vehicleId)
                        .title("차량 등록이 승인되었습니다.")
                        .content("차량번호 " + plate + " 등록이 승인되었습니다.")
                        .linkPath("/resident/" + complexId + "/vehicles")
                        .build());

            } else if ("REJECTED".equals(status)) {
                notificationService.createNotification(NotificationPostReq.builder()
                        .receiverUserId(userId)
                        .complexId(complexId)
                        .type("VEHICLE_REGISTRATION_REJECTED")
                        .targetType("VEHICLE")
                        .targetId(vehicleId)
                        .title("차량 등록이 반려되었습니다.")
                        .content("차량번호 " + plate + " 등록이 반려되었습니다. 관리사무소에 문의해 주세요.")
                        .linkPath("/resident/" + complexId + "/vehicles")
                        .build());
            }
        } catch (Exception e) {
            log.error("Failed to consume vehicle status changed event. message={}", message, e);
        }
    }

    // 세대 매칭 결과 이벤트 → SIGNUP_APPROVED / SIGNUP_REJECTED 단건 알림
    @KafkaListener(topics = KafkaTopics.HOUSEHOLD, groupId = "notification-service-household-match")
    public void consumeHouseholdMatchResultEvent(String message) {
        try {
            EventEnvelope<JsonNode> envelope = objectMapper.readValue(
                    message, new TypeReference<EventEnvelope<JsonNode>>() {}
            );

            String eventType = envelope.getEventType() == null ? null : envelope.getEventType().name();

            if (!EventType.HOUSEHOLD_MATCH_APPROVED.name().equals(eventType)
                    && !EventType.HOUSEHOLD_MATCH_REJECTED.name().equals(eventType)) {
                return;
            }

            JsonNode payload = envelope.getPayload();
            Long userId    = longValue(payload, "userId");
            Long complexId = longValue(payload, "complexId");

            if (userId == null || complexId == null) {
                log.warn("household match result event missing userId or complexId. message={}", message);
                return;
            }

            boolean approved = EventType.HOUSEHOLD_MATCH_APPROVED.name().equals(eventType);
            notificationService.createNotification(NotificationPostReq.builder()
                    .receiverUserId(userId)
                    .complexId(complexId)
                    .type(approved ? "SIGNUP_APPROVED" : "SIGNUP_REJECTED")
                    .targetType("USER")
                    .targetId(userId)
                    .title(approved ? "입주 신청이 승인되었습니다." : "입주 신청이 반려되었습니다.")
                    .content(approved ? "단지 입주 신청이 승인되었습니다. 서비스를 이용하실 수 있습니다."
                                      : "단지 입주 신청이 반려되었습니다. 관리사무소에 문의해 주세요.")
                    .linkPath("/resident/" + complexId + "/home")
                    .build());

        } catch (Exception e) {
            log.error("Failed to consume household match result event. message={}", message, e);
        }
    }

    // BoardEventEnvelope의 payload 필드를 추출한다 (EventEnvelope와 동일한 구조)
    private JsonNode payloadNode(String message) throws Exception {
        JsonNode root = objectMapper.readTree(message);
        JsonNode payload = root.path("payload");
        return payload.isMissingNode() || payload.isNull() ? root : payload;
    }

    private Long longValue(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asLong();
    }
}
