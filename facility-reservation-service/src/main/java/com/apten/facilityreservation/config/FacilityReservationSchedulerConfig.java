package com.apten.facilityreservation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// 예약/선점 스케줄러는 outbox 설정과 무관하게 동작해야 하므로 서비스 내부에서 scheduling을 켠다.
@Configuration
@EnableScheduling
public class FacilityReservationSchedulerConfig {
}
