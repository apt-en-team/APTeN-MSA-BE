package com.apten.auth.infrastructure.config;

import com.apten.auth.security.CustomOAuth2UserService;
import com.apten.auth.security.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

// auth-service 보안 설정 — 공개 경로 허용 및 OAuth2 로그인 연결
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // OAuth2 공급자에서 받은 사용자 정보를 내부 User와 연결하는 서비스
    private final CustomOAuth2UserService customOAuth2UserService;

    // OAuth2 로그인 성공 후 JWT 발급 핸들러
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    // 공개 경로 허용 + form 로그인, HTTP Basic 비활성화 — REST API 방식으로만 인증
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll()
                        // MASTER 전용 경로 — MANAGER 계정 생성
                        .requestMatchers("/api/admin/master/**").hasRole("MASTER")
                        // MANAGER 전용 경로 — ADMIN 계정 생성
                        .requestMatchers("/api/admin/manager/**").hasRole("MANAGER")
                        .anyRequest().authenticated()
                )
                // OAuth2 소셜 로그인 설정 연결
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                // 공급자별 사용자 정보를 읽고 내부 User와 연결하는 서비스
                                .userService(customOAuth2UserService)
                        )
                        // 로그인 성공 시 JWT 발급 핸들러 연결
                        .successHandler(oAuth2SuccessHandler)
                );

        return http.build();
    }
}