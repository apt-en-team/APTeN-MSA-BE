package com.apten.parkingvehicle.config;

import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

// 스케줄러 분산 락을 켜는 설정 클래스이다.
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10s")
public class ShedLockConfig {

    // Redis 기반 분산 락 프로바이더 빈 생성
    @Bean
    public RedisLockProvider redisLockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory, "apten");
    }
}
