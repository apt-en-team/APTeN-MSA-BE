package com.apten.notification.scheduler;

import com.apten.notification.application.model.response.NotificationCleanupRes;
import com.apten.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 읽음 처리된 알림 중 30일 경과 데이터를 매일 자동으로 삭제한다.
// 미읽음 알림은 cleanupOldNotifications() 내부 정책에 따라 삭제 대상에서 제외된다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "apten.scheduler.notification-cleanup",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class NotificationCleanupScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "${apten.scheduler.notification-cleanup.cron:0 0 3 * * *}")
    public void cleanup() {
        log.info("[NotificationCleanup] 오래된 알림 정리 스케줄 실행 시작");
        try {
            NotificationCleanupRes result = notificationService.cleanupOldNotifications();
            log.info("[NotificationCleanup] 정리 완료. deletedCount={}, executedAt={}",
                    result.getDeletedCount(), result.getExecutedAt());
        } catch (Exception exception) {
            log.error("[NotificationCleanup] 오래된 알림 정리 중 오류 발생", exception);
        }
    }
}
