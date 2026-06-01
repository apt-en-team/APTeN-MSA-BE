package com.apten.notification.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// notification-service의 기술 설정을 모아둘 기본 Config 클래스
@Configuration
@EnableScheduling
@EnableConfigurationProperties(NotificationPushProperties.class)
public class NotificationAppConfig {
}
