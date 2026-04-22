package com.apten.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// 게이트웨이 설정 활성화
@Configuration
@EnableConfigurationProperties(GatewayAuthProperties.class)
public class GatewayConfig {
}
