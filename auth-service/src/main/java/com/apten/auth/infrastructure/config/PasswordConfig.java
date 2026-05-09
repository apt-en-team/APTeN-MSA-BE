package com.apten.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 비밀번호 암호화 빈 설정 — SecurityConfig 순환 참조 방지를 위해 분리
@Configuration
public class PasswordConfig {

    @Bean
    // BCrypt 단방향 해시 암호화 — salt 적용으로 같은 비밀번호도 매번 다른 해시값 생성
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}