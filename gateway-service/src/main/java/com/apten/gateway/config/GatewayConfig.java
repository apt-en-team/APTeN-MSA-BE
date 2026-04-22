package com.apten.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// gateway 전용 설정 클래스를 스프링 빈으로 등록하는 진입 설정
// 보안 필터와 예외 처리기가 GatewayAuthProperties를 바로 주입받을 수 있게 연결한다
@Configuration
@EnableConfigurationProperties(GatewayAuthProperties.class)
public class GatewayConfig {
}
