package com.apten.notification.application.service;

import com.apten.notification.infrastructure.config.NotificationPushProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPushService {

    private final NotificationPushProperties properties;

    public boolean send(Long notificationId) {
        // HTTP 개발 환경에서는 FCM 실제 발송을 막고 DB/WebSocket 알림만 사용한다
        if (!properties.isFcmEnabled()) {
            return false;
        }

        // Firebase Admin SDK 연동은 HTTPS 배포 후 fcm-enabled=true 환경에서 연결한다
        return false;
    }
}
