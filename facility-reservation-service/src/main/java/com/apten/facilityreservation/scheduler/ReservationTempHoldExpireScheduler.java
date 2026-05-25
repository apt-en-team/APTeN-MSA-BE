package com.apten.facilityreservation.scheduler;

import com.apten.facilityreservation.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 임시 선점 만료 스케줄러는 Redis TTL이 지난 HOLDING 행을 주기적으로 EXPIRED로 보정한다.
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "apten.scheduler.temp-hold-expire",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ReservationTempHoldExpireScheduler {

    private final ReservationService reservationService;

    // Redis TTL 기본값이 10분이므로 60초 주기로 실행해 만료 행을 적시에 정리한다.
    @Scheduled(fixedDelayString = "${apten.scheduler.temp-hold-expire.fixed-delay-ms:60000}")
    public void expireSeatHolds() {
        reservationService.expireSeatHolds();
    }
}
