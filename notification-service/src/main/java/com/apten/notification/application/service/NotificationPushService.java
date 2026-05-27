package com.apten.notification.application.service;

import com.apten.notification.domain.entity.FcmToken;
import com.apten.notification.domain.entity.Notification;
import com.apten.notification.domain.repository.FcmTokenRepository;
import com.apten.notification.domain.repository.NotificationRepository;
import com.apten.notification.infrastructure.config.NotificationPushProperties;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPushService {

    private final NotificationPushProperties properties;
    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final ObjectProvider<FirebaseMessaging> firebaseMessagingProvider;

    @Transactional
    public boolean send(Long notificationId) {
        log.info("[FCM] 발송 처리 진입. notificationId={}", notificationId);

        // HTTP 개발 환경에서는 FCM 실제 발송을 막고 DB/WebSocket 알림만 사용한다
        if (!properties.isFcmEnabled()) {
            log.info("[FCM] 발송 비활성화 상태라 생략합니다. notificationId={}, fcmEnabled={}",
                    notificationId, properties.isFcmEnabled());
            return false;
        }

        if (notificationId == null) {
            log.warn("[FCM] notificationId 없음 - 발송 생략");
            return false;
        }

        FirebaseMessaging firebaseMessaging = firebaseMessagingProvider.getIfAvailable();
        if (firebaseMessaging == null) {
            log.warn("[FCM] FirebaseMessaging bean 없음 - 발송 생략");
            return false;
        }

        return notificationRepository.findById(notificationId)
                .map(notification -> sendToActiveTokens(firebaseMessaging, notification))
                .orElseGet(() -> {
                    log.warn("[FCM] 알림 없음 - notificationId={}", notificationId);
                    return false;
                });
    }

    private boolean sendToActiveTokens(FirebaseMessaging firebaseMessaging, Notification notification) {
        List<FcmToken> activeTokens = fcmTokenRepository.findByUserIdAndIsActive(notification.getUserId(), true);
        log.info("[FCM] 활성 토큰 조회. userId={}, tokenCount={}", notification.getUserId(), activeTokens.size());

        if (activeTokens.isEmpty()) {
            // 토큰이 없어도 DB 알림과 WebSocket 흐름은 이미 완료되었으므로 조용히 생략한다
            log.debug("[FCM] 활성 토큰 없음 - notificationId={}, userId={}", notification.getId(), notification.getUserId());
            return false;
        }

        List<String> tokenValues = activeTokens.stream()
                .map(FcmToken::getTokenValue)
                .filter(StringUtils::hasText)
                .toList();
        if (tokenValues.isEmpty()) {
            log.debug("[FCM] 발송 가능한 토큰 문자열 없음 - notificationId={}", notification.getId());
            return false;
        }

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getContent())
                        .build())
                .putAllData(buildDataPayload(notification))
                .addAllTokens(tokenValues)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            deactivateInvalidTokens(activeTokens, response.getResponses());
            log.info("[FCM] 발송 완료. success={}, failure={}",
                    response.getSuccessCount(), response.getFailureCount());
            if (response.getFailureCount() > 0) {
                log.warn("[FCM] 일부 토큰 발송 실패 - notificationId={}, success={}, failure={}",
                        notification.getId(), response.getSuccessCount(), response.getFailureCount());
            }
            return response.getSuccessCount() > 0;
        } catch (FirebaseMessagingException exception) {
            // FCM 장애가 원본 알림 저장이나 WebSocket 전송 결과를 되돌리면 안 된다
            log.warn("[FCM] Firebase 발송 실패 - notificationId={}, error={}", notification.getId(), exception.getMessage());
            return false;
        }
    }

    private Map<String, String> buildDataPayload(Notification notification) {
        Map<String, String> data = new HashMap<>();
        put(data, "notificationId", notification.getId());
        put(data, "title", notification.getTitle());
        put(data, "content", notification.getContent());
        put(data, "type", notification.getType() == null ? null : notification.getType().name());
        put(data, "typeCode", notification.getType() == null ? null : notification.getType().getCode());
        put(data, "typeValue", notification.getType() == null ? null : notification.getType().getValue());
        put(data, "targetType", notification.getTargetType() == null ? null : notification.getTargetType().name());
        put(data, "targetTypeCode", notification.getTargetType() == null ? null : notification.getTargetType().getCode());
        put(data, "targetTypeValue", notification.getTargetType() == null ? null : notification.getTargetType().getValue());
        put(data, "targetId", notification.getTargetId());
        put(data, "linkPath", notification.getLinkPath());
        put(data, "payloadJson", notification.getPayloadJson());
        put(data, "createdAt", notification.getCreatedAt());
        return data;
    }

    private void put(Map<String, String> data, String key, Object value) {
        if (value != null) {
            data.put(key, String.valueOf(value));
        }
    }

    private void deactivateInvalidTokens(List<FcmToken> activeTokens, List<SendResponse> responses) {
        for (int i = 0; i < responses.size(); i++) {
            SendResponse response = responses.get(i);
            if (response.isSuccessful()) {
                continue;
            }

            FirebaseMessagingException exception = response.getException();
            if (isInvalidToken(exception)) {
                // Firebase가 영구적으로 못 쓰는 토큰이라고 알려주면 다음 발송 대상에서 제외한다
                activeTokens.get(i).deactivate();
                log.warn("[FCM] 무효 토큰 비활성화 - tokenId={}, error={}",
                        activeTokens.get(i).getId(), exception.getMessagingErrorCode());
            } else if (exception != null) {
                log.warn("[FCM] 토큰 발송 실패 - tokenId={}, error={}",
                        activeTokens.get(i).getId(), exception.getMessage());
            }
        }
    }

    private boolean isInvalidToken(FirebaseMessagingException exception) {
        if (exception == null) {
            return false;
        }
        MessagingErrorCode errorCode = exception.getMessagingErrorCode();
        return errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT;
    }
}
