package com.apten.facilityreservation.config;

import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

// 스케줄러 분산 락 설정
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10s")
public class ShedLockConfig {

    @Bean
    public RedisLockProvider redisLockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory, "apten");
    }
}
