package com.apten.notification.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// notification-service의 기술 설정을 모아둘 기본 Config 클래스
// PushProperties를 Bean으로 등록해 FCM guard 설정을 서비스에서 읽을 수 있게 한다
@Configuration
@EnableConfigurationProperties(NotificationPushProperties.class)
public class NotificationAppConfig {
}
