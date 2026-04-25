package com.apten.auth.infrastructure.config;

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

    // OAuth2 로그인 성공 후 JWT 발급 핸들러 — 순환 참조 방지로 주석
    // private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    // 공개 경로 허용 + form 로그인, HTTP Basic 비활성화 — REST API 방식으로만 인증
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable) // form 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)  // HTTP Basic 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                );

        // ClientRegistrationRepository 에러 방지 — OAuth2 설정 완료 전까지 주석 유지
        /*
        .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
        )
        .oauth2Client(Customizer.withDefaults());
        */

        return http.build();
    }

    // BCryptPasswordEncoder → PasswordConfig 클래스로 분리
}