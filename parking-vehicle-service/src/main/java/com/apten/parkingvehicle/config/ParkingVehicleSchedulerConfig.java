package com.apten.parkingvehicle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// 주차 서비스 내부 스케줄러를 켜는 설정 클래스이다.
@Configuration
@EnableScheduling
public class ParkingVehicleSchedulerConfig {
}
