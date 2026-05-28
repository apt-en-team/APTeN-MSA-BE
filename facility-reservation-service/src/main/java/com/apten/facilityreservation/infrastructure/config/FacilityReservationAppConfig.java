package com.apten.facilityreservation.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// facility-reservation-service의 기술 설정을 모아둘 기본 Config 클래스
// 내부 서비스 호출 주소 같은 기술 설정을 이 패키지에서 함께 관리한다
@Configuration
@EnableConfigurationProperties(NotificationServiceProperties.class)
public class FacilityReservationAppConfig {
}
