package com.apten.facilityreservation.scheduler;

import com.apten.facilityreservation.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 예약 완료 스케줄러는 종료된 CONFIRMED 예약을 주기적으로 COMPLETED로 보정한다.
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "apten.scheduler.reservation-complete",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ReservationCompleteScheduler {

    private final ReservationService reservationService;

    // 운영 중 반복 실행되므로 service 내부 batch size로 한 번에 처리하는 수를 제한한다.
    @Scheduled(fixedDelayString = "${apten.scheduler.reservation-complete.fixed-delay-ms:60000}")
    @SchedulerLock(name = "reservation-complete", lockAtMostFor = "55s", lockAtLeastFor = "1s")
    public void completeReservations() {
        reservationService.completeReservations();
    }
}
