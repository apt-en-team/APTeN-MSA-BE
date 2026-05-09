package com.apten.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis 연결 및 직렬화 설정
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory); // application.yml의 Redis 연결 정보 주입

        // StringRedisSerializer 적용으로 사람이 읽을 수 있는 문자열 형태로 저장
        template.setKeySerializer(new StringRedisSerializer()); // key String 직렬화
        template.setValueSerializer(new StringRedisSerializer()); // value String 직렬화

        return template;
    }
}