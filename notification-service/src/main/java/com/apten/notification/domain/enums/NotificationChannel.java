package com.apten.notification.domain.enums;

// 후속 notification_delivery_log에서 FCM 발송 이력을 구분하기 위한 채널값
public enum NotificationChannel {
    // WebSocket 발송 이력은 1차에서 저장하지 않으므로 FCM만 둔다
    FCM
}
