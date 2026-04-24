package com.apten.auth.infrastructure.config;

import com.apten.auth.security.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// auth-service 보안 설정 — 공개 경로 허용 및 OAuth2 로그인 연결
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // OAuth2 로그인 성공 후 JWT 발급을 담당하는 핸들러
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    // 공개 경로 (/api/auth/**, /oauth2/**, /login/**) 인증 없이 허용
    // 나머지 경로는 인증 필요, OAuth2 로그인 성공 시 oAuth2SuccessHandler 호출
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

    @Bean
    // BCrypt 단방향 해시 암호화 — salt 적용으로 같은 비밀번호도 매번 다른 해시값 생성
    // 이메일 로그인 시 입력 비밀번호와 DB 해시값 비교에 사용
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}