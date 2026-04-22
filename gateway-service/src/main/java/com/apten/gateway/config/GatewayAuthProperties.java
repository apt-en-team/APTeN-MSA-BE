package com.apten.gateway.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// gateway가 auth-service 기준 토큰을 해석할 때 필요한 최소 설정 모음
// 공개 경로와 JWT 검증용 비밀키를 application.yml에서 받아 보안 설정과 필터가 함께 사용한다
@Getter
@Setter
@ConfigurationProperties(prefix = "gateway.auth")
public class GatewayAuthProperties {

    // 인증 없이 통과시킬 공개 경로 목록
    private List<String> excludedPaths = new ArrayList<>();

    // auth-service가 토큰 서명에 사용한 값과 맞춰야 하는 JWT 비밀키
    private String jwtSecret;
}
