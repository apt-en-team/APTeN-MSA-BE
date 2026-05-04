package com.apten.auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EntityScan(basePackages = {"com.apten.auth", "com.apten.common"})
@EnableJpaRepositories(basePackages = {"com.apten.auth", "com.apten.common"})
@SpringBootApplication(scanBasePackages = {"com.apten.auth", "com.apten.common"})
public class AuthServiceApplication {

    public static void main(String[] args) {
        // 1. .env 파일을 로드
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // 2. 로드된 변수들을 시스템 프로퍼티로 등록
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
