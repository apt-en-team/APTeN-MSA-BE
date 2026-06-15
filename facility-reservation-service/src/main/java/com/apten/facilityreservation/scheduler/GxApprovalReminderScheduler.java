package com.apten.facilityreservation.scheduler;

import com.apten.facilityreservation.application.service.GxProgramService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// GX 시작 7일 전, 승인 대기 신청이 있는 프로그램에 대해 관리자에게 리마인더 알림을 발송한다.
// 매일 1회 실행되므로 startDate가 정확히 7일 후인 프로그램에만 알림이 발송된다.
// 서비스 재기동이 같은 날에 여러 번 발생하면 중복 발송될 수 있으나, 리마인더 특성상 허용한다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "apten.scheduler.gx-approval-reminder",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class GxApprovalReminderScheduler {

    private final GxProgramService gxProgramService;

    @Scheduled(cron = "${apten.scheduler.gx-approval-reminder.cron:0 0 9 * * *}")
    @SchedulerLock(name = "gx-approval-reminder", lockAtMostFor = "30m", lockAtLeastFor = "1m")
    public void sendApprovalReminders() {
        LocalDate targetDate = LocalDate.now().plusDays(7);
        log.info("[GxApprovalReminder] 승인 리마인더 스케줄 실행. targetDate={}", targetDate);
        gxProgramService.sendApprovalReminderNotifications(targetDate);
    }
}
