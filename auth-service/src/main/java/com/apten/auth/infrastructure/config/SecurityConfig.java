package com.apten.auth.infrastructure.config;

import com.apten.auth.security.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

// auth-service가 어떤 경로를 열어두고 어떤 로그인 방식을 연결할지 정하는 보안 설정
// OAuth2 로그인 성공 후 SuccessHandler로 흐름이 이어지도록 연결하는 시작 지점이다
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // OAuth2 로그인 성공 후 JWT 발급 단계로 넘기는 핸들러
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // 인증 없이 열어둘 경로와 OAuth2 로그인 진입 방식을 한 번에 설정한다
    // 보호 경로에 접근하면 인증이 필요하고 로그인 성공 시 SuccessHandler가 호출된다
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                )
                .oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}
