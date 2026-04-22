package com.apten.gateway.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// 게이트웨이 인증 관련 설정
@Getter
@Setter
@ConfigurationProperties(prefix = "gateway.auth")
public class GatewayAuthProperties {

    private List<String> excludedPaths = new ArrayList<>();
    private String jwtSecret;
}
