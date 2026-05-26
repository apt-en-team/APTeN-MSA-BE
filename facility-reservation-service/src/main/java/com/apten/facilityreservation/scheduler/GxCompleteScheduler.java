package com.apten.facilityreservation.scheduler;

import com.apten.facilityreservation.application.service.GxReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// GX 이용완료 스케줄러는 종료된 GX 프로그램의 WAITING/CONFIRMED 예약을 COMPLETED로 보정한다.
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "apten.scheduler.gx-complete",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class GxCompleteScheduler {

    private final GxReservationService gxReservationService;

    @Scheduled(fixedDelayString = "${apten.scheduler.gx-complete.fixed-delay-ms:60000}")
    public void completeGxReservations() {
        gxReservationService.completeGxReservations();
    }
}
