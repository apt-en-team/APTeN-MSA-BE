package com.apten.parkingvehicle.application.scheduler;

import com.apten.parkingvehicle.application.service.ZoneCounterReconcileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// SENSOR 단지 zone 점유 카운터 드리프트를 주기적으로 보정하는 스케줄러이다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "apten.scheduler.zone-counter-reconcile.enabled", havingValue = "true", matchIfMissing = true)
public class ZoneCounterReconcileScheduler {

    // 카운터 재동기화 응용 서비스
    private final ZoneCounterReconcileService zoneCounterReconcileService;

    // 활성 OCCUPIED 센서 수 기준으로 zone 카운터를 주기 보정
    @Scheduled(fixedDelayString = "${apten.scheduler.zone-counter-reconcile.fixed-delay-ms:30000}")
    @SchedulerLock(name = "zone-counter-reconcile", lockAtMostFor = "25s", lockAtLeastFor = "1s")
    public void reconcile() {
        int reconciledZones = zoneCounterReconcileService.reconcileAll();
        if (reconciledZones > 0) {
            log.info("[zone-counter-reconcile] reconciledZones={}", reconciledZones);
        }
    }
}
