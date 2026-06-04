package com.apten.facilityreservation.scheduler;

import com.apten.facilityreservation.application.service.GxProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 종료일이 지난 GX 프로그램을 매일 자정에 CLOSED 상태로 일괄 변경한다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "apten.scheduler.gx-program-close",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class GxProgramCloseScheduler {

    private final GxProgramService gxProgramService;

    @Scheduled(cron = "${apten.scheduler.gx-program-close.cron:0 0 0 * * *}")
    public void closeExpiredPrograms() {
        log.info("[GxProgramClose] 만료 GX 프로그램 종료 처리 시작");
        gxProgramService.closeExpiredPrograms();
        log.info("[GxProgramClose] 만료 GX 프로그램 종료 처리 완료");
    }
}
