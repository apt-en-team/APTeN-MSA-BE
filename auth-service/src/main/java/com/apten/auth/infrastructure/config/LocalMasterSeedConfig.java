package com.apten.auth.infrastructure.config;

import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.enums.SignupType;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 로컬 개발 환경에서만 MASTER 계정을 자동으로 보정하는 설정이다.
@Configuration
@Profile("local")
@RequiredArgsConstructor
public class LocalMasterSeedConfig {

    private static final String MASTER_EMAIL = "master@apten.com";
    private static final String MASTER_PASSWORD = "master1234!";
    private static final String MASTER_NAME = "마스터";
    private static final String MASTER_PHONE = "01012345678";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 로컬 실행 시 MASTER 계정이 없으면 1회 생성한다.
    @Bean
    public CommandLineRunner localMasterSeedRunner() {
        return args -> {
            if (userRepository.findByEmail(MASTER_EMAIL).isPresent()) {
                return;
            }

            User masterUser = User.builder()
                    .email(MASTER_EMAIL)
                    .passwordHash(passwordEncoder.encode(MASTER_PASSWORD))
                    .name(MASTER_NAME)
                    .phone(MASTER_PHONE)
                    .role(UserRole.MASTER)
                    .status(UserStatus.ACTIVE)
                    .signupType(SignupType.EMAIL)
                    .isPhoneVerified(true)
                    .isEmailVerified(true)
                    .loginFailCount(0)
                    .isDeleted(false)
                    .build();

            userRepository.save(masterUser);
        };
    }
}
