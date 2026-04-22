package com.apten.notification.application.service;

import com.apten.notification.application.model.request.NotificationOwnerCheckReq;
import com.apten.notification.application.model.request.NotificationPostReq;
import com.apten.notification.application.model.request.NotificationSearchReq;
import com.apten.notification.application.model.response.NotificationCleanupRes;
import com.apten.notification.application.model.response.NotificationGetPageRes;
import com.apten.notification.application.model.response.NotificationOwnerCheckRes;
import com.apten.notification.application.model.response.NotificationPostRes;
import com.apten.notification.application.model.response.NotificationReadAllRes;
import com.apten.notification.application.model.response.NotificationReadRes;
import com.apten.notification.application.model.response.NotificationUnreadCountRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 앱 내 알림 조회와 읽음 처리, 내부 생성 흐름을 담당하는 응용 서비스
@Service
public class NotificationService {

    public NotificationGetPageRes getNotificationList(NotificationSearchReq request) {
        // TODO: 내 알림 목록 조회 로직 구현
        return NotificationGetPageRes.empty(request.getPage(), request.getSize());
    }

    public NotificationUnreadCountRes getUnreadCount() {
        // TODO: 미읽음 알림 수 조회 로직 구현
        return NotificationUnreadCountRes.builder()
                .unreadCount(0)
                .build();
    }

    public NotificationReadRes readNotification(String notificationUid) {
        // TODO: 알림 소유자 검증 후 읽음 처리 구현
        return NotificationReadRes.builder()
                .notificationUid(notificationUid)
                .isRead(true)
                .readAt(LocalDateTime.now())
                .build();
    }

    public NotificationReadAllRes readAllNotifications() {
        // TODO: 전체 읽음 처리 구현
        return NotificationReadAllRes.builder()
                .updatedCount(0)
                .readAt(LocalDateTime.now())
                .build();
    }

    public NotificationPostRes createNotification(NotificationPostReq request) {
        // TODO: 내부 알림 생성 처리
        return NotificationPostRes.builder()
                .receiverUserUid(request.getReceiverUserUid())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public NotificationCleanupRes cleanupOldNotifications() {
        // TODO: 30일 지난 알림 삭제 스케줄러 처리
        return NotificationCleanupRes.builder()
                .deletedCount(0)
                .executedAt(LocalDateTime.now())
                .build();
    }

    public NotificationOwnerCheckRes checkNotificationOwner(NotificationOwnerCheckReq request) {
        // TODO: 알림 접근 권한 검증 정책 구현
        return NotificationOwnerCheckRes.builder()
                .allowed(false)
                .build();
    }
}
