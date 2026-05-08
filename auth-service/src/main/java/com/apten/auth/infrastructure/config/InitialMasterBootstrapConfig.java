package com.apten.auth.infrastructure.config;

import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.enums.SignupType;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 설정값으로 명시적으로 켰을 때만 최초 MASTER 계정을 보정한다.
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.bootstrap.master", name = "enabled", havingValue = "true")
public class InitialMasterBootstrapConfig {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${apten.bootstrap.master.email:}")
    private String masterEmail;

    @Value("${apten.bootstrap.master.password:}")
    private String masterPassword;

    @Value("${apten.bootstrap.master.name:}")
    private String masterName;

    @Value("${apten.bootstrap.master.phone:}")
    private String masterPhone;

    // 애플리케이션 시작 시 최초 MASTER 계정이 없으면 1회 생성한다.
    @Bean
    public CommandLineRunner initialMasterBootstrapRunner() {
        return args -> {
            // 필수 설정이 빠진 경우 계정을 생성하지 않고 로그만 남긴다.
            if (isBlank(masterEmail) || isBlank(masterPassword) || isBlank(masterName) || isBlank(masterPhone)) {
                log.warn("MASTER bootstrap 설정이 누락되어 계정 생성을 건너뜁니다. email={}, name={}, phone={}",
                        safeValue(masterEmail), safeValue(masterName), safeValue(masterPhone));
                return;
            }

            // 같은 이메일 계정이 이미 있으면 중복 생성하지 않는다.
            if (userRepository.findByEmail(masterEmail).isPresent()) {
                log.info("MASTER bootstrap 대상 계정이 이미 존재하여 생성을 건너뜁니다. email={}", masterEmail);
                return;
            }

            User masterUser = User.builder()
                    .email(masterEmail)
                    .passwordHash(passwordEncoder.encode(masterPassword))
                    .name(masterName)
                    .phone(masterPhone)
                    .role(UserRole.MASTER)
                    .status(UserStatus.ACTIVE)
                    .signupType(SignupType.EMAIL)
                    .isPhoneVerified(true)
                    .isEmailVerified(true)
                    .loginFailCount(0)
                    .isDeleted(false)
                    .build();

            userRepository.save(masterUser);
            log.info("최초 MASTER 계정을 생성했습니다. email={}", masterEmail);
        };
    }

    // 설정 문자열이 비어 있는지 확인한다.
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // 로그에는 빈 값 여부만 드러나도록 안전하게 변환한다.
    private String safeValue(String value) {
        return isBlank(value) ? "(blank)" : value;
    }
}
