package com.apten.notification.application.service;

import com.apten.notification.application.model.request.NotificationPushPostReq;
import com.apten.notification.application.model.response.NotificationPushPostRes;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 내부 테스트/긴급 재발송용 FCM 수동 발송
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final NotificationPushService notificationPushService;

    // 푸시 발송
    public NotificationPushPostRes sendPushNotification(NotificationPushPostReq request) {
        // 내부 API는 DB 알림 ID만 넘기고 실제 발송 가능 여부는 PushService 설정이 판단한다
        Long notificationId = request == null ? null : request.getNotificationId();
        boolean sent = notificationPushService.send(notificationId);
        return NotificationPushPostRes.builder()
                .notificationId(notificationId)
                .sent(sent)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
