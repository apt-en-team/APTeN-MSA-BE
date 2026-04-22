package com.apten.gateway.security;

import com.apten.gateway.config.GatewayAuthProperties;
import com.apten.gateway.filter.TokenAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

// gateway에서 공개 경로와 보호 경로를 나누고 인증 필터를 연결하는 보안 설정
// auth-service 로그인 경로는 열어 두고 나머지 서비스 요청은 JWT 확인 뒤에만 통과시킨다
@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    // 보호 경로에서 JWT를 읽고 사용자 헤더를 붙이는 필터
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    // 인증 실패를 공통 JSON 응답으로 내려주는 진입점
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // 공개 경로 목록과 JWT 검증 기준을 담은 설정 객체
    private final GatewayAuthProperties gatewayAuthProperties;

    // 공개 경로는 바로 통과시키고 그 외 모든 경로는 인증을 요구하는 보안 체인을 만든다
    // 보호 경로 요청은 TokenAuthenticationFilter를 거친 뒤에만 downstream 서비스로 전달된다
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec = http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange();

        gatewayAuthProperties.getExcludedPaths().forEach(path -> authorizeExchangeSpec.pathMatchers(path).permitAll());

        return authorizeExchangeSpec
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(tokenAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    // 로컬 프런트 개발 환경에서 gateway를 직접 호출할 수 있도록 CORS를 연다
    // Authorization 헤더와 사용자 헤더를 함께 다루기 위해 credentials와 exposed headers도 맞춘다
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "X-User-Id", "X-User-Role"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
