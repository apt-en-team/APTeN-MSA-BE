package com.apten.apartmentcomplex.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// apartment-complex-service의 기술 설정을 모아둘 기본 Config 클래스
@Configuration
@EnableConfigurationProperties({JusoProperties.class})
public class ApartmentComplexAppConfig {
}
