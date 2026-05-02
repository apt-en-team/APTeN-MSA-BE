package com.apten.apartmentcomplex.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// apartment-complex-service의 기술 설정을 모아둘 기본 Config 클래스
// 이후 JPA, MyBatis, Kafka 관련 세부 설정이 생기면 이 패키지에서 이어서 확장한다
@Configuration
@EnableConfigurationProperties(JusoProperties.class)
public class ApartmentComplexAppConfig {
}
