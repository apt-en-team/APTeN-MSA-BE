package com.apten.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Gateway Redis 연결 및 직렬화 설정
// WebFlux 기반이라 일반 RedisTemplate 대신 ReactiveRedisTemplate 사용
@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        // key, value 모두 String 직렬화 — Redis CLI에서 바로 확인 가능
        RedisSerializationContext<String, String> context =
                RedisSerializationContext.<String, String>newSerializationContext(
                        new StringRedisSerializer()
                ).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}