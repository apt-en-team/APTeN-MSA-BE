package com.apten.parkingvehicle.application.scheduler;

import com.apten.parkingvehicle.application.model.response.VisitorVehicleExpireRes;
import com.apten.parkingvehicle.application.service.VisitorVehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 미출차 방문차량 자동 만료 스케줄러이다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "apten.scheduler.visitor-expire.enabled", havingValue = "true")
public class VisitorExpireScheduler {

    private final VisitorVehicleService visitorVehicleService;

    // 유예 기간 지난 APPROVED 방문차량을 EXPIRED로 일괄 정리
    @Scheduled(cron = "${apten.scheduler.visitor-expire.cron}")
    @SchedulerLock(name = "visitor-expire", lockAtMostFor = "5m", lockAtLeastFor = "30s")
    public void runExpireSweep() {
        VisitorVehicleExpireRes result = visitorVehicleService.expireVisitorVehicles();
        log.info("[visitor-expire] expiredCount={}, executedAt={}",
                result.getExpiredCount(), result.getExecutedAt());
    }
}
